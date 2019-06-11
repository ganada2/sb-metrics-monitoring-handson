package com.example.demo;

import java.net.URI;
import java.net.URLEncoder;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@CrossOrigin("*")
@RestController
public class LoveCalculatorController {
	private static final String LOVE_CALCULATOR_CONSUMER_URL = "http://localhost:8082/love-calculator-consumer";
	private static final String YES_OR_NO_CONSUMER_URL = "http://localhost:8083/yes-or-no-consumer";
	
	@RequestMapping(value = "/love-calculator", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> getLoveCalculatorWithYesOrNoImage(@RequestParam(value = "fname") String fname, @RequestParam(value = "sname") String sname) throws Exception {
		
		// REST 서비스 호출을 위해 RestTemplate 사
		RestTemplate restTemplate =  new RestTemplate();
		
		URI uri = new URI(LOVE_CALCULATOR_CONSUMER_URL + "?fname=" + URLEncoder.encode(fname, "UTF-8") + "&sname=" + URLEncoder.encode(sname, "UTF-8"));
		
		ResponseEntity<JsonNode> love_calc_response = null;
		ResponseEntity<JsonNode> yes_or_no_response = null;
		
		try {
			love_calc_response = restTemplate.getForEntity(uri, JsonNode.class);
			
			JsonNode percentage = love_calc_response.getBody().get("percentage");
			
			String force;
			if(percentage.asInt() >= 50) {
				force = "yes";
			} else if(percentage.asInt() >= 30 && percentage.asInt() < 50){
				force = "maybe";
			} else {
				force = "no";
			}
			
			yes_or_no_response = restTemplate.getForEntity(YES_OR_NO_CONSUMER_URL + "?force="+force, JsonNode.class);
			
			((ObjectNode) love_calc_response.getBody()).put("status", "success");
			((ObjectNode) love_calc_response.getBody()).put("image", yes_or_no_response.getBody().get("image").asText());
			
			
		} catch (HttpServerErrorException e) {
			JsonNode node = JsonNodeFactory.instance.objectNode();
			((ObjectNode) node).put("status", "error");
			((ObjectNode) node).put("message", e.getMessage());
			((ObjectNode) node).put("code", e.getRawStatusCode());
			
			return ResponseEntity.ok().body(node);
		} catch (Exception e) {
			JsonNode node = JsonNodeFactory.instance.objectNode();
			((ObjectNode) node).put("status", "error");
			((ObjectNode) node).put("message", e.getMessage());
			((ObjectNode) node).put("code", "99999");
			
			return ResponseEntity.ok().body(node);
		}
		
		return ResponseEntity.ok().body(love_calc_response.getBody());
	}
}