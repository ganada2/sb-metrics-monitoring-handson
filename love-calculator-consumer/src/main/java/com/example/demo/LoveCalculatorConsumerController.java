package com.example.demo;

import java.net.URI;
import java.net.URLEncoder;

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
public class LoveCalculatorConsumerController {
	private static final String LOVE_CALCULATOR_OPEN_API_URL = "https://love-calculator.p.rapidapi.com/getPercentage";
	private static final String X_RAPIDAPI_HOST = "love-calculator.p.rapidapi.com";
	private static final String X_RAPIDAPI_KEY = "83fb8e2e14msh8f387c18efbef02p12fac5jsnd9be04c755dd";
	
	
	@RequestMapping(value = "/love-calculator-consumer", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> getPercentage(@RequestParam(value = "fname") String fname, @RequestParam(value = "sname") String sname) throws Exception {
		
		// REST 서비스 호출을 위해 RestTemplate 사
		RestTemplate restTemplate =  new RestTemplate();
		
		URI uri = new URI(LOVE_CALCULATOR_OPEN_API_URL + "?fname=" + URLEncoder.encode(fname, "UTF-8") + "&sname=" + URLEncoder.encode(sname, "UTF-8"));
		
		// Love Calculator API 호출을 위 헤더 설
		final HttpHeaders headers = new HttpHeaders();
		headers.add("X-RapidAPI-Host", X_RAPIDAPI_HOST);
		headers.add("X-RapidAPI-Key", X_RAPIDAPI_KEY);
		
		final HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// 헤더와 함께 Love Calculator REST API 호출
		ResponseEntity<JsonNode> love_calc_response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
		
		return love_calc_response;
	}
}
