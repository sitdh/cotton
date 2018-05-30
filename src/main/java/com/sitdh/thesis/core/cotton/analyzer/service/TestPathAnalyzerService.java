package com.sitdh.thesis.core.cotton.analyzer.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("TestPathAnalyzerService")
public class TestPathAnalyzerService implements TestPathAnalyzer {

    private TinkerGraph graph;

    @Getter @Setter
    private GraphTraversalSource g;

    public TestPathAnalyzerService() {
        graph = TinkerGraph.open();
        g = graph.traversal();
    }

    public Vertex crateVertex(String name) {

        return graph.addVertex(T.label, name, "name", name);
    }

    public void linkVertices(Vertex from, Vertex to, String... properties) {
    }

    @Override
    public List<String> analyzed() {

        log.debug("Analyzed");
        return null;
    }

}
