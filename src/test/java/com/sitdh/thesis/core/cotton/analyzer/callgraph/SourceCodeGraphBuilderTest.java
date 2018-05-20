package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.Before;
import org.junit.Test;

import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

public class SourceCodeGraphBuilderTest {
	
	private SourceCodeGraphBuilder graphBuilder;
	
	@Before
	public void setup() throws ClassFormatException, IOException, PackageNotInterestedException {
		ClassParser cp = new ClassParser(
				"/Users/sitdh/Projects/thesis/fabric/jenkinsci/data/workspace/fibre-tax-income_master-"
				+ "527YMND3KSSUC4GKBS2OCH3YXCAOHTGNM3THFPPD37N7WGGIA4KQ/"
				+ "target/classes/com/sitdh/thesis/example/TaxableApplication.class");
		
		graphBuilder = SourceCodeGraphBuilder.analyzedForClass(cp.parse(), "com.sitdh.thesis.example");
		graphBuilder.start();
	}
	
	@Test
	public void graphCanCreate() {
		GraphTraversalSource g = graphBuilder.graph().get();
		assertThat(g).isNotNull();
	}

	@Test
	public void graphCreate() {
		Optional<GraphTraversalSource> go = graphBuilder.graph();
		assertTrue(go.isPresent());
		
		GraphTraversalSource g = go.get();
		assertEquals(4, g.V().count().next().intValue());
		assertEquals(3, g.E().count().next().intValue());
	}

}
