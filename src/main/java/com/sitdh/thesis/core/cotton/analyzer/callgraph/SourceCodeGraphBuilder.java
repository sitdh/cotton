package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

import java.util.Optional;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

import lombok.extern.java.Log;

@Log
public class SourceCodeGraphBuilder extends EmptyVisitor {

	private JavaClass javaClass;
	
	private TinkerGraph graph;
	
	private GraphTraversalSource g;
	
	private Vertex primaryVertex;
	
	private String interestedPackage;
	
	private SourceCodeGraphBuilder(JavaClass jc, String interestedPackage) throws PackageNotInterestedException { 
		this.javaClass = jc;
		this.interestedPackage = interestedPackage;
		this.graph = TinkerGraph.open();
		this.g = graph.traversal();
		
		if (!javaClass.getClassName().startsWith(interestedPackage)) {
			throw new PackageNotInterestedException();
		}
		
		primaryVertex = graph.addVertex("type", "C", "name", javaClass.getClassName());
	}
	
	public static SourceCodeGraphBuilder analyzedForClass(JavaClass jc, String interestedPackage) throws PackageNotInterestedException {
		return new SourceCodeGraphBuilder(jc, interestedPackage);
	}
	
	public void start() {
		javaClass.getConstantPool().accept(this);
	}
	
	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			if ((null == constant) || (7 != constant.getTag())) continue;
			
			String referencedClass = constantPool.constantToString(constant);
			if (!referencedClass.startsWith(interestedPackage)) continue;
			
			Vertex referencedVertex = graph.addVertex("type", "C", "name", referencedClass);
			
			primaryVertex.addEdge("linked", referencedVertex, "weight", 1f);
			g.addE("linked").from(primaryVertex).to(referencedVertex);
			
			System.out.println(javaClass.getClassName() + " -> " + referencedClass);
		}
		
		log.info(g.toString());
	}
	
	public Optional<GraphTraversalSource> graph() {
		return Optional.of(g);
	}

}