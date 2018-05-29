package com.sitdh.thesis.core.cotton.analyzer.dsl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;

@GremlinDsl
public interface TestPathTraversalDSL<S, E> extends GraphTraversal.Admin<S, E> {

}
