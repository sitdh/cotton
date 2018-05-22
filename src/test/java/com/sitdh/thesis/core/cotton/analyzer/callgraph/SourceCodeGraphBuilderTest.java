package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.Before;
import org.junit.Test;

import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;
import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

public class SourceCodeGraphBuilderTest {
	
	private SourceCodeGraphBuilder graphBuilder;
	
	@Before
	public void setup() throws ClassFormatException, IOException, PackageNotInterestedException {
		ClassParser cp = new ClassParser(
				"/Users/sitdh/workspace/cotton/target/classes/com/sitdh/thesis/core/cotton/analyzer/callgraph/SourceCodeGraphBuilder.class");
		
		graphBuilder = SourceCodeGraphBuilder.analyzedForClass(cp.parse(), "com.sitdh.thesis.core.cotton");
		graphBuilder.start();
	}
	
	@Test
	public void should_create_graph() {
		GraphTraversalSource g = graphBuilder.graph().get();
		assertThat(g).isNotNull();
	}

	@Test
	public void graph_created() {
		Optional<GraphTraversalSource> go = graphBuilder.graph();
		assertTrue(go.isPresent());
		
		GraphTraversalSource g = go.get();
		assertEquals(7, g.V().count().next().intValue());
		assertEquals(6, g.E().count().next().intValue());
	}
	
	@Test
	public void digraph_should_exists() throws NoGraphToAnalyzeException {
		String digraph = graphBuilder.getDigraph();
		assertThat(digraph.startsWith("digraph"), is(true));
		assertThat(digraph.contains("subgraph"), is(true));
	}

}
