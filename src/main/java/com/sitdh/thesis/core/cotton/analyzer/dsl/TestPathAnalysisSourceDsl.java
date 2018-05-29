package com.sitdh.thesis.core.cotton.analyzer.dsl;

import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class TestPathAnalysisSourceDsl extends GraphTraversalSource {
	
	public TestPathAnalysisSourceDsl(Graph graph) {
		super(graph);
	}

	public TestPathAnalysisSourceDsl(Graph graph, final TraversalStrategies traversalStrategies) {
		super(graph, traversalStrategies);
	}
	
	public GraphTraversal<Vertex, Vertex> paths(String... path) {
		return null;
	}

}
