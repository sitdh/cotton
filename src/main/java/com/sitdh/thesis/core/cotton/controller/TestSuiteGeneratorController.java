package com.sitdh.thesis.core.cotton.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sitdh.thesis.core.cotton.analyzer.service.util.VectorUtils;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.entity.Vector;
import com.sitdh.thesis.core.cotton.database.repository.ProjectRepository;
import com.sitdh.thesis.core.cotton.database.repository.VectorRepository;
import com.sitdh.thesis.core.cotton.service.entity.TestPathPackage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestSuiteGeneratorController {
	
//	private VectorRepository vectorRepo;
	
	private ProjectRepository projectRrepo;
	
	private HttpHeaders headers;

	@Autowired
	public TestSuiteGeneratorController(
			HttpHeaders headers,
			ProjectRepository projectRepo, 
			VectorRepository vectorRepo) {
		
		this.projectRrepo = projectRepo;
//		this.vectorRepo = vectorRepo;
		this.headers = headers;
	}
	
	@GetMapping("/code/test-paths/{slug}")
	public ResponseEntity<List<TestPathPackage>> generateTestPath(@PathVariable String slug) {
		log.debug("Find for " + slug);
		Optional<Project> p = this.projectRrepo.findById(slug);
		List<Vector> vectors = Lists.newArrayList();
		
		Graph graph = TinkerGraph.open();
		GraphTraversalSource g = null;
		List<Vector> main = null;
		
		List<TestPathPackage> testPaths = Lists.newArrayList();
		
		Map<String, Vertex> visitedVertex = Maps.newHashMap();
		
		if (p.isPresent()) { 
			vectors = VectorUtils.reduceCyclicPath(p.get().getVectors());
			
			main = VectorUtils.allMainClass(vectors).get();
			
			int i = 1;
			
			for (Vector v : main) {
				Vertex v1, v2;
				
				if (visitedVertex.containsKey(v.getSource())) {
					v1 = visitedVertex.get(v.getSource());
				} else {
					v1 = graph.addVertex(T.id, i++, T.label, v.getSource(), "name", v.getSource());
					visitedVertex.put(v.getSource(), v1);
				}
				
				if (visitedVertex.containsKey(v.getTarget())) {
					v2 = visitedVertex.get(v.getTarget());
				} else {
					v2 = graph.addVertex(T.id, i++, T.label, v.getTarget(), "name", v.getTarget());
					visitedVertex.put(v.getTarget(), v2);
				}
				
				v1.addEdge(v.getEdge(), v2, "name", v.getEdge());
			}
			
			vectors.removeAll(main);
			
			for (Vector v : vectors) {
				Vertex v1, v2;
				
				if (visitedVertex.containsKey(v.getSource())) {
					v1 = visitedVertex.get(v.getSource());
				} else {
					v1 = graph.addVertex(T.id, i++, T.label, v.getSource(), "name", v.getSource());
					visitedVertex.put(v.getSource(), v1);
				}
				
				if (visitedVertex.containsKey(v.getTarget())) {
					v2 = visitedVertex.get(v.getTarget());
				} else {
					v2 = graph.addVertex(T.id, i++, T.label, v.getTarget(), "name", v.getTarget());
					visitedVertex.put(v.getTarget(), v2);
				}
				
				v1.addEdge(v.getEdge(), v2, "name", v.getEdge());
			}
			
			g = graph.traversal();
			g.V(1).repeat(outE().inV().simplePath()).until(outE().count().is(0)).path().toStream().forEach(px -> {
				List<String> pathDetail = Lists.newArrayList();
				
				log.debug(String.valueOf(px));
				
				px.objects().stream().forEach(o -> {
					pathDetail.add(String.valueOf(o));
				});
				
				TestPathPackage tpp = TestPathPackage.builder()
						.pathId(UUID.randomUUID().toString())
						.steps(pathDetail)
						.build();
				
				testPaths.add(tpp);
			});
		}
		
		return new ResponseEntity<>(testPaths, headers, HttpStatus.OK);
	}
}
