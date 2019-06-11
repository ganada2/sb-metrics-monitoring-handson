package com.example.demo;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class YesOrNoConsumerController {
	private static String YES_OR_NO_OPEN_API_URL = "https://yesno.wtf/api";
	
	@RequestMapping(value = "/yes-or-no-consumer", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> getYesorNo(@RequestParam(value = "force") String force) throws Exception {
		
		// REST 서비스 호출을 위해 RestTemplate 사
		RestTemplate restTemplate =  new RestTemplate();
		
		URI uri = new URI(YES_OR_NO_OPEN_API_URL + "?force=" + force);
		
		// Yes or No REST API 호출
		ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);
		
		return response;
	}
}