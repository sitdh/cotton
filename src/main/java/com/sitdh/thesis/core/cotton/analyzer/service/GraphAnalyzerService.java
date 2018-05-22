package com.sitdh.thesis.core.cotton.analyzer.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sitdh.thesis.core.cotton.analyzer.callgraph.SourceCodeGraphBuilder;
import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;
import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String analyzed(String slug, String branch, String interestedPackage) throws NoGraphToAnalyzeException {
		
		String digraph = null;
		
		try {
			
			Optional<Path> mainClass = this.locationUtil
					.listClassFiles(slug, branch)
					.stream()
					.filter(LocationUtils::filterForMainClass)
					.findFirst();
			
			if (mainClass.isPresent()) {
				log.info("Main class exists");
				JavaClass jc = new ClassParser(mainClass.get().toString()).parse();
				digraph = SourceCodeGraphBuilder.analyzedForClass(jc, interestedPackage).getDigraph();
				
				log.info("Main: " + mainClass.get().toString());
			} else {
				log.info("No main class found");
				log.info("----------");
				log.warning("No main class present");
				log.info("----------");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PackageNotInterestedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
