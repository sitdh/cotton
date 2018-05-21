package com.sitdh.thesis.core.cotton.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitdh.thesis.core.cotton.analyzer.data.ConstantData;
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;
import com.sitdh.thesis.core.cotton.analyzer.service.GraphAnalyzer;

import lombok.extern.java.Log;

@Log
@RestController
public class SourceCodeAnalyzerServiceController {
	
	private HttpHeaders headers;
	
	@Autowired
	@Qualifier("SimpleConstantsCollectorAnalyzer")
	private ConstantAnalyzer constantCollector;
	
	@Autowired
	@Qualifier("SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;
	
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

	@GetMapping("/code/graph/{slug}/{branch}")
	public @ResponseBody ResponseEntity<List<String>> analyzeSourcecodeForGraph(@PathVariable String slug, @PathVariable String branch) {
		graphAnalyzer.sourceLocation(slug, branch);
		
		List<String> graphStructure = graphAnalyzer.analyzed();
		
		return  new ResponseEntity<>(graphStructure, headers, HttpStatus.OK);
	}
}
