package com.sitdh.thesis.core.cotton.analyzer.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sitdh.thesis.core.cotton.analyzer.callgraph.SourceCodeGraphBuilder;
import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.extern.java.Log;

@Log
@Service("SimpleGraphAnalyzer")
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerService implements GraphAnalyzer {
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String sourceLocation;
	
	private LocationUtils locationUtil;
	
	@Autowired
	public GraphAnalyzerService(LocationUtils locationUtil) {
		this.locationUtil = locationUtil;
	}
	
	@Override
	public List<String> analyzed() {
		return null;
	}
	
	@Override
	public String analyzed(String slug, String branch, String interestedPackage) throws NoGraphToAnalyzeException {
		
		String digraph = null;
		
		try {
			
			List<Path> allClasses = this.locationUtil.listClassFiles(slug, branch);
			digraph = SourceCodeGraphBuilder.analyzedForProject(allClasses, interestedPackage)
					.analyze()
					.getDigraph();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return digraph;
	}

	@Override
	public Optional<String> sourceLocation(String slug, String branch) {
		String location = null;
		
		try {
			location = locationUtil.getProjectWorkspace(slug, branch);
		} catch(FileNotFoundException e) {
			log.throwing(this.getClass().getName(), "sourceLocation", e);
		}
		
		return Optional.ofNullable(location);
	}
	
}
