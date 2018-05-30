package com.sitdh.thesis.core.cotton.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sitdh.thesis.core.cotton.database.repository.ProjectRepository;
import com.sitdh.thesis.core.cotton.generator.TestCaseBuilder;
import com.sitdh.thesis.core.cotton.generator.TestcaseGeneratorProcessBuilder;
import com.sitdh.thesis.core.cotton.service.entity.TestCaseGeneratorResponsePackage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestCaseGenerateController {
	
	@Autowired
	private HttpHeaders headers;
	
	private ProjectRepository projectRepo;
	
	private TestCaseBuilder testcaseGeneratorPB;
	
	private TestcaseGeneratorProcessBuilder testcaseGenerator;
	
	@Autowired
	public TestCaseGenerateController(
			ProjectRepository projectRepo,
			TestCaseBuilder testcaseGeneratorPB,
			TestcaseGeneratorProcessBuilder testcaseGenerator
			) {
		
		this.projectRepo = projectRepo;
		this.testcaseGeneratorPB = testcaseGeneratorPB;
		this.testcaseGenerator = testcaseGenerator;
	}
	
	@GetMapping("/testcase/generator/{slug}")
	public ResponseEntity<TestCaseGeneratorResponsePackage> generateTestCaseForProject(@PathVariable String slug) {
		log.debug("Getting start");
		// TODO Need revise
		TestCaseGeneratorResponsePackage tcgPackage = new TestCaseGeneratorResponsePackage(); 
		tcgPackage.setGenerated("Done");
		
		try {
			this.testcaseGenerator.build(null);
			tcgPackage.setTestmessage("Fine");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tcgPackage.setTestmessage("Failure");
		}
		
		return new ResponseEntity<>(tcgPackage, headers, HttpStatus.OK);
	}

}
