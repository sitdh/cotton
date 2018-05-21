package com.sitdh.thesis.core.cotton.analyzer.service;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service("SimpleGraphAnalyzer")
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerService implements GraphAnalyzer {
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String sourceLocation;
	
	@Override
	public List<String> analyzed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> sourceLocation(String slug, String branch) {
		// TODO Auto-generated method stub
		File directory = new File(sourceLocation);
		File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		
		Optional<String> location = Optional.of("");
		
		for(File dir : subdirs) {
			if (this.filter(dir.getName(), slug, branch)) {
				log.info("Directory: " + dir.getName());
				location = Optional.of(dir.getName());
			}
		}
		
		return location;
	}
	
	private boolean filter(String currentDir, String slug, String branch) {
		
		return !currentDir.contains("@")
				&& currentDir
				.startsWith(
						String.format(
								"%s_%s",  
								slug, 
								branch)); 
	}
	
}
