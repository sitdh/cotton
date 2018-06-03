package com.sitdh.thesis.core.cotton.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitdh.thesis.core.cotton.analyzer.data.GraphVector;
import com.sitdh.thesis.core.cotton.database.entity.DynamicVector;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.repository.DynamicVectorRepository;
import com.sitdh.thesis.core.cotton.database.repository.ProjectRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ExecutionLoggerServiceController {
	
	private DynamicVectorRepository dynamicLogRepo;
	private ProjectRepository projectRepo;
	
	@Autowired
	private HttpHeaders headers;

	@Autowired
	ExecutionLoggerServiceController(DynamicVectorRepository dynamicLogRepo, ProjectRepository projectRepo) {
		this.dynamicLogRepo = dynamicLogRepo;
		this.projectRepo = projectRepo;
	}
	
	@PostMapping("/log")
	public ResponseEntity<?> depositDynamicLoggingMessage(@RequestBody GraphVector logInfo) {
		String slug = "fibre-grading-demo";
		
		log.debug("Getting log: " + slug);
		Optional<Project> optionalProjectInfo = this.projectRepo.findById(slug);
		
		DynamicVector dv = null;
		
		HttpStatus status = HttpStatus.CONFLICT;
		
		if (optionalProjectInfo.isPresent()) {
			Project project = optionalProjectInfo.get();
			
			GraphVector gv = GraphVector.builder()
					.source(logInfo.getSource())
					.edge(logInfo.getEdge())
					.target(logInfo.getTarget())
					.build();
			
			dv = new DynamicVector(gv);
			dv.setProject(project);
			
			dv = dynamicLogRepo.save(dv);
			
			status = HttpStatus.ACCEPTED;
			
		}
		
		return new ResponseEntity<>(dv, headers, status);
	}
}
