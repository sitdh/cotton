package com.sitdh.thesis.core.cotton.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitdh.thesis.core.cotton.analyzer.data.ConstantData;
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;

import lombok.extern.java.Log;

@Log
@RestController
public class SourceCodeAnalyzerServiceController {
	
	private HttpHeaders headers;
	
	@Autowired
	private ConstantAnalyzer constantCollector;
	
	
	
	@Autowired
	public SourceCodeAnalyzerServiceController(HttpHeaders headers) {
		log.info("Constructor reached");
		this.headers = headers;
	}
	
	@GetMapping("/code/constant")
	public ResponseEntity<List<ConstantData>> constantsCollector() {
		
		List<ConstantData> data = constantCollector.analyzed();
		return new ResponseEntity<>(data, headers, HttpStatus.OK);
	}

	@GetMapping("/code/graph")
	public @ResponseBody ResponseEntity<Map<String, String>> analyzeSourcecodeForGraph() {
		Map<String, String> structure = Map.of("A", "B", "B", "C", "C", "D"); 
		
		return  new ResponseEntity<Map<String, String>>(structure, headers, HttpStatus.OK);
	}
}
