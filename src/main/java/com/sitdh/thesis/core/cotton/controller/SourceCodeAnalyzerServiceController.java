package com.sitdh.thesis.core.cotton.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sitdh.thesis.core.cotton.analyzer.callgraph.SourceCodeGraphAnalysis;
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;
import com.sitdh.thesis.core.cotton.analyzer.service.GraphAnalyzer;
import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;
import com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.repository.ConstantCollectionRepository;
import com.sitdh.thesis.core.cotton.database.repository.ControlFlowGraphRepository;
import com.sitdh.thesis.core.cotton.database.repository.ProjectRepository;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;
import com.sitdh.thesis.core.cotton.service.entity.ConstantPackage;
import com.sitdh.thesis.core.cotton.service.entity.ControlFlowGraphPackage;

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
	private ControlFlowGraphRepository cfgRepo;
	private ProjectRepository projectRepo;
	
	@Autowired
	public SourceCodeAnalyzerServiceController(
			HttpHeaders headers, 
			ConstantCollectionRepository constantRepo,
			ProjectRepository projectRepo,
			ControlFlowGraphRepository cfgRepo) {
		
		log.info("Constructor reached");
		this.headers = headers;
		this.constantRepo = constantRepo;
		this.projectRepo = projectRepo;
		this.cfgRepo = cfgRepo;
	}
	
	@GetMapping("/code/constant/{slug}")
	public ResponseEntity<List<ConstantPackage>> constantsCollector(@PathVariable String slug) {
		List<ConstantPackage> cps = Lists.newArrayList();
		List<ConstantCollection> cc = this.constantRepo.findByProjectId(slug);
		Map<String, List<String>> groupedConstants = cc.stream().collect(
				Collectors.groupingBy(
						ConstantCollection::getType, 
						Collectors.mapping(ConstantCollection::getValue, Collectors.toList()))
				);
		
		groupedConstants.forEach((k, v) -> {
			cps.add(new ConstantPackage(StringUtils.capitalize(k), v));
		});
		
		HttpStatus hs = groupedConstants.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK ;
		
		return new ResponseEntity<>(cps, headers, hs);
	}
	
	@GetMapping("/code/cfg/{slug}")
	public ResponseEntity<List<ControlFlowGraphPackage>> controlFlowGraph(@PathVariable String slug) {
		Optional<Project> p = projectRepo.findById(slug);
		List<ControlFlowGraphPackage> cfgp = Lists.newArrayList();
		
		if (p.isPresent()) {
			List<ControlFlowGraph> cfgs = p.get().getGraphs();
			
			cfgp = this.cleanCallgraph(cfgs);
		}
		
		return new ResponseEntity<>(cfgp, headers, HttpStatus.OK);
	}

	@GetMapping("/code/graph/{slug}/{branch}")
	public @ResponseBody ResponseEntity<Map<String, String>> analyzeSourcecodeForGraph(
			@PathVariable String slug, 
			@PathVariable String branch, 
			@RequestParam("p") String interestedpackage,
			@RequestParam("update") Optional<Boolean> isUpdate) throws NoGraphToAnalyzeException {
		
		Project project = projectRepo.findById(slug)
				.filter(p -> p.getBranch().equals(branch))
				.filter(p -> p.getInterestedPackage().equals(interestedpackage))
				.orElseGet(Project::new);
		
		project.setProjectId(slug);
		project.setBranch(branch);
		project.setInterestedPackage(interestedpackage);
		
		project = projectRepo.save(project);
		
		Map<String, String> graphStructure = Maps.newHashMap();
		
		HttpHeaders h = headers;
		HttpStatus hs = HttpStatus.OK;
		
		boolean update = isUpdate.orElse(false);
		
		if (StringUtils.isBlank(project.getGraphClass()) || StringUtils.isBlank(project.getGraphMethod()) || update) {
			log.info("Package: " + interestedpackage);
			
			this.constantRepo.deleteAll();
			this.cfgRepo.deleteAll();
			
			graphStructure = graphAnalyzer.analyzedStructure(project);
			
			project.setGraphClass(graphStructure.get(SourceCodeGraphAnalysis.DIGRAPH_CLASS_TYPE));
			project.setGraphMethod(graphStructure.get(SourceCodeGraphAnalysis.DIGRAPH_METHOD_TYPE));
			
			projectRepo.save(project);
			
		} else {
			graphStructure.put(SourceCodeGraphAnalysis.DIGRAPH_CLASS_TYPE, project.getGraphClass());
			graphStructure.put(SourceCodeGraphAnalysis.DIGRAPH_METHOD_TYPE, project.getGraphMethod());
		}
		
		return  new ResponseEntity<>(graphStructure, h, hs);
	}
	
	private List<ControlFlowGraphPackage> cleanCallgraph(List<ControlFlowGraph> cfgs) {
		List<ControlFlowGraphPackage> cfg = Lists.newArrayList();
		Set<String> className = Sets.newHashSet();
		
		cfgs.stream().forEach(c -> {
			className.add(c.getCgId().getClassName());
		});
		
		className.stream().forEach(cn -> {
			List<ControlFlowGraph> cc = cfgs.stream()
					.filter(c -> cn.equals(c.getCgId().getClassName())
					)
					.collect(Collectors.toList());
			cfg.add(new ControlFlowGraphPackage(cn, cc));
		});
		
		return cfg;
	}
}
