package com.sitdh.thesis.core.cotton.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SourceCodeAnalyzerServiceController {
	
	private HttpHeaders headers;
	
	@Autowired
	public SourceCodeAnalyzerServiceController(HttpHeaders headers) {
		log.debug("Constructor reached");
		this.headers = headers;
	}
	
	@GetMapping("/code/constant")
	public ResponseEntity<List<String>> constantsCollector() {
		
		List<String> messages = Arrays.asList("A", "B", "C");
		
		return new ResponseEntity<>(messages, headers, HttpStatus.OK);
	}

	@GetMapping("/code/graph")
	public @ResponseBody ResponseEntity<Map<String, String>> analyzeSourcecodeForGraph() {
		Map<String, String> structure = Map.of("A", "B", "B", "C", "C", "D"); 
		
		return  new ResponseEntity<Map<String, String>>(structure, headers, HttpStatus.OK);
	}
}
