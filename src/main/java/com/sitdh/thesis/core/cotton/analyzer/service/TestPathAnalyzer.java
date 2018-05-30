package com.sitdh.thesis.core.cotton.analyzer.service;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public interface TestPathAnalyzer extends Analyzer<List<String>>{

    public Vertex crateVertex(String name);

    public void linkVertices(Vertex from, Vertex to, String... properties);

}
