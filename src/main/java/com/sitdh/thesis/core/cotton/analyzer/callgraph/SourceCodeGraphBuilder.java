package com.sitdh.thesis.core.cotton.analyzer.callgraph;

// import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

import lombok.extern.java.Log;

@Log
public class SourceCodeGraphBuilder extends EmptyVisitor {

	private JavaClass javaClass;
	
	private ConstantPoolGen constants;
	
	private TinkerGraph graph;
	
	private GraphTraversalSource g;
	
	private Vertex primaryVertex;
	
	private String interestedPackage;
	
	private String graphFormat = "";
	
	private List<String> graphStructure;
	
	private List<String> methodStructure;
	
	private String digraphMessage = "";
	
	private SourceCodeGraphBuilder(JavaClass jc, String interestedPackage) throws PackageNotInterestedException { 
		this.javaClass = jc;
		this.interestedPackage = interestedPackage;
		this.graph = TinkerGraph.open();
		this.g = graph.traversal();
		this.constants = new ConstantPoolGen(javaClass.getConstantPool());
		
		if (!javaClass.getClassName().startsWith(interestedPackage)) {
			throw new PackageNotInterestedException();
		}
		
		String shortName = this.getShortname(javaClass.getClassName());
		
		graphFormat = "\"C:" + shortName + "\" -> \"C:%s\"";
		primaryVertex = graph.addVertex("type", "C", "name", shortName);
		
		graphStructure = new ArrayList<String>();
		
		methodStructure = new ArrayList<String>();
	}
	
	public static SourceCodeGraphBuilder analyzedForClass(JavaClass jc, String interestedPackage) throws PackageNotInterestedException {
		return new SourceCodeGraphBuilder(jc, interestedPackage);
	}
	
	public void start() {
		javaClass.getConstantPool().accept(this);
		Method[] methods = javaClass.getMethods();
		for(int i = 0; i < methods.length; i++) {
			if (!"<init>".equals(methods[i].getName())) 
				methods[i].accept(this);
		}
	}
	
	public String getDigraph() {
		digraphMessage += this.convertToDigraph( graphStructure);
		return this.getShortname(this.digraphMessage);
	}
	
	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, javaClass.getClassName(), constants);
		
		if (mg.isAbstract() || mg.isNative()) return;
		
		List<String> p = SourcecodeMethodGraphBuilder
				.analyzeForMethod(mg, javaClass, g, interestedPackage)
				.start()
				.getTraversalStack();
		
		methodStructure.addAll(p);
	}
	
	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			
			if (!this.isConformedToCondition(Optional.ofNullable(constant))) continue;
			String referencedClass = constantPool.constantToString(constant);
			
			if(!referencedClass.startsWith(interestedPackage)) continue;
			
			this.updateGraph(referencedClass);
		}
		
	}
	
	public Optional<GraphTraversalSource> graph() {
		return Optional.of(g);
	}
	
	private boolean isConformedToCondition(Optional<Constant> constant) {
		
		return constant.isPresent() 
				&& (7 == constant.get().getTag());
	}
	
	private GraphTraversalSource updateGraph(String referencedClass) {
		Vertex referencedVertex = graph.addVertex("type", "C", "name", this.getShortname(referencedClass));
		
		primaryVertex.addEdge("linked", referencedVertex, "weight", 1f);
		g.addE("linked").from(primaryVertex).to(referencedVertex);
		
		graphStructure.add(String.format(graphFormat, this.getShortname(referencedClass)));
		
		log.fine(String.format(graphFormat, this.getShortname(referencedClass)));
		
		return g;
	}
	
	private String getShortname(String className) {
		return StringUtils.replaceAll(className, interestedPackage, "*");
	}
	
	private String convertToDigraph(List<String> graph) {
		return "digraph { \n\t " 
		+ StringUtils.replace(
				StringUtils.join(graph, "\n\t"),
				"'",
				"\""
				)
			+ "}";
	}

}