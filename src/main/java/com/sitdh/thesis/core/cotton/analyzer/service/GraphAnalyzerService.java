package com.sitdh.thesis.core.cotton.analyzer.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.graph.Graph;
import com.sitdh.thesis.core.cotton.analyzer.data.GraphVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sitdh.thesis.core.cotton.analyzer.callgraph.SourceCodeGraphAnalysis;
import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;
import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.repository.ConstantCollectionRepository;
import com.sitdh.thesis.core.cotton.database.repository.ControlFlowGraphRepository;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("SimpleGraphAnalyzer")
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerService implements GraphAnalyzer {
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String sourceLocation;
	
	private LocationUtils locationUtil;
	
	private ConstantCollectionRepository constantCollectorRepo;
	
	private ControlFlowGraphRepository cfgRepo;
	
	private String projectSlug;

	private List<GraphVector> connections;
	
	@Autowired
	public GraphAnalyzerService(LocationUtils locationUtil, 
			ConstantCollectionRepository constantCollectorRepository,
			ControlFlowGraphRepository cfgRepo) {
		
		this.locationUtil = locationUtil;
		this.constantCollectorRepo = constantCollectorRepository;
		this.cfgRepo = cfgRepo;

    this.connections = Lists.newArrayList();
	}
	
	@Override
	public List<String> analyzed() {
		return null;
	}
	
	@Override
	public String analyzed(String slug, String branch, String interestedPackage) throws NoGraphToAnalyzeException {
		
		String digraph = null;
		projectSlug = slug;
		
		try {
			
			List<Path> allClasses = this.locationUtil.listClassFiles(slug, branch);
			
			SourceCodeGraphAnalysis scga = new SourceCodeGraphAnalysis.SourceCodeGraphAnalysisBuilder()
					.classListing(allClasses)
					.build()
					.analyze();

			scga.getConstantCollector().forEach(this::saveCollectedConstants);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return digraph;
	}
	
	public Map<String, String> analyzedStructure(Project p) throws NoGraphToAnalyzeException {
		SourceCodeGraphAnalysis scga = null;
		
		projectSlug = p.getProjectId();
		
		try {
			log.debug("PROJECT SLUG: ", projectSlug);
			List<Path> allClasses = this.locationUtil.listClassFiles(p.getProjectId(), p.getBranch());
			
			scga = new SourceCodeGraphAnalysis(allClasses, p);
			scga.analyze();
				
			scga.getConstantCollector().forEach(this::saveCollectedConstants);
			scga.getControlFlowGraphs().stream().forEach(s -> {
				System.out.println(
						String.format("%s", s.getProjectId().getProjectId())
						);
				if (null == s.getProjectId()) s.setProjectId(p);
				this.cfgRepo.save(s);
			});

			scga.getConnections().forEach(c -> {
				log.debug("Connection: from - ", c.getSource(), " [", c.getEdge(), "] ", c.getTarget());
			});

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return scga.getGraphs();
	}

	@Override
	public Optional<String> sourceLocation(String slug, String branch) {
		String location = null;
		
		try {
			location = locationUtil.getProjectWorkspace(slug, branch);
		} catch(FileNotFoundException e) {
			log.error("From source location", e.getClass().getName());
		}
		
		return Optional.ofNullable(location);
	}
	
	private void saveCollectedConstants(ConstantCollection cc) {
		cc.setProjectId(projectSlug);
		constantCollectorRepo.save(cc);
		
		log.debug("Found", cc.getValue(), " => ", cc.getType(), "and save to project ", projectSlug);
	}
	
}
