package com.sitdh.thesis.core.cotton.analyzer.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;

import lombok.extern.java.Log;

@Log
@Service("SimpleGraphAnalyzer")
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerService implements GraphAnalyzer {
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String sourceLocation;
	
	private LocationUtils locationUtil;
	
	@Autowired
	GraphAnalyzerService(LocationUtils locationUtil) {
		this.locationUtil = locationUtil;
	}
	
	@Override
	public List<String> analyzed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> sourceLocation(String slug, String branch) {
		String location = null;
		
		try {
			location = locationUtil.getProjectWorkspace(slug, branch);
		} catch(FileNotFoundException e) {
			log.throwing(this.getClass().getName(), sourceLocation, e);
		}
		
		return Optional.ofNullable(location);
	}
	
}
