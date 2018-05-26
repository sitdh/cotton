package com.sitdh.thesis.core.cotton.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;
import com.sitdh.thesis.core.cotton.analyzer.service.GraphAnalyzer;
import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;
import com.sitdh.thesis.core.cotton.database.repository.ConstantCollectionRepository;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SourceCodeAnalyzerServiceController {
	
	private HttpHeaders headers;
	
	@Autowired
	@Qualifier("SimpleConstantsCollectorAnalyzer")
	private ConstantAnalyzer constantCollector;
	
	@Autowired
	@Qualifier("SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;
	
	private ConstantCollectionRepository constantRepo;
	
	@Autowired
	public SourceCodeAnalyzerServiceController(HttpHeaders headers, ConstantCollectionRepository constantRepo) {
		log.info("Constructor reached");
		this.headers = headers;
		this.constantRepo = constantRepo;
	}
	
	@GetMapping("/code/constant/{slug}")
	public ResponseEntity<Map<String, List<String>>> constantsCollector(@PathVariable String slug) {
		
		List<ConstantCollection> cc = this.constantRepo.findByProjectId(slug);
		Map<String, List<String>> groupedConstants = cc.stream().collect(
				Collectors.groupingBy(
						ConstantCollection::getType, 
						Collectors.mapping(ConstantCollection::getValue, Collectors.toList()))
				);
		
		HttpStatus hs = groupedConstants.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK ;
		
		return new ResponseEntity<>(groupedConstants, headers, hs);
	}

	@GetMapping("/code/graph/{slug}/{branch}")
	public @ResponseBody ResponseEntity<String> analyzeSourcecodeForGraph(@PathVariable String slug, @PathVariable String branch, @RequestParam("p") String interestedpackage) {
		
		String graphStructure;
		
		HttpHeaders h = headers;
		HttpStatus hs = HttpStatus.OK;
		
		log.info("Package: " + interestedpackage);
		
		try {
			this.constantRepo.deleteAll();
			
			graphStructure = graphAnalyzer.analyzed(
					slug, 
					branch, 
					interestedpackage);
			
		} catch (NoGraphToAnalyzeException e) {
			graphStructure = "No data found";
			hs = HttpStatus.NO_CONTENT;
			h.setContentType(MediaType.TEXT_PLAIN);
			
		}
		
		return  new ResponseEntity<>(graphStructure, h, hs);
	}
}
