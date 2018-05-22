package com.sitdh.thesis.core.cotton.analyzer.callgraph;

// import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import com.sitdh.thesis.core.cotton.analyzer.service.util.ClassSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.MethodSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.SubgraphTemplate;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;
import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

import lombok.extern.java.Log;

@Log
public class SourceCodeGraphBuilder extends EmptyVisitor {
	
	public static final String DIGRAPH_CLASS_TYPE = "class";
	
	public static final String DIGRAPH_METHOD_TYPE = "methods";

	private JavaClass javaClass;
	
	private ConstantPoolGen constants;
	
//	private TinkerGraph graph;
	
	private GraphTraversalSource g;
	
	private String interestedPackage;
	
	private String graphFormat = "";
	
	private List<String> graphStructure;
	
	private String digraphMessage = "";
	
	private SourceCodeGraphBuilder(JavaClass jc, String interestedPackage) throws PackageNotInterestedException { 
		this.javaClass = jc;
		this.interestedPackage = interestedPackage;
//		this.graph = TinkerGraph.open();
//		this.g = graph.traversal();
		this.constants = new ConstantPoolGen(javaClass.getConstantPool());
		
		if (!javaClass.getClassName().startsWith(interestedPackage)) {
			throw new PackageNotInterestedException();
		}
		
		String shortName = this.getShortname(javaClass.getClassName());
		
		graphFormat = "\"C:" + shortName + "\" -> \"C:%s\";";
//		primaryVertex = graph.addVertex("type", "C", "name", shortName);
		
		graphStructure = new ArrayList<String>();
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
	
	public String getDigraph() throws NoGraphToAnalyzeException {
		digraphMessage += this.convertToDigraph(graphStructure);
		return this.getShortname(this.digraphMessage);
	}
	
	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, javaClass.getClassName(), constants);
		
		if (mg.isAbstract() || mg.isNative()) return;
		
		List<String> p = SourcecodeMethodGraphBuilder
				.analyzeForMethod(mg, javaClass, g, interestedPackage)
				.start()
				.getTraversalStack();
		
		graphStructure.addAll(p);
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
//		Vertex referencedVertex = graph.addVertex("type", "C", "name", this.getShortname(referencedClass));
		
//		primaryVertex.addEdge("linked", referencedVertex, "weight", 1f);
//		g.addE("linked").from(primaryVertex).to(referencedVertex);
		
		graphStructure.add(String.format(graphFormat, this.getShortname(referencedClass)));
		
		log.fine(String.format(graphFormat, this.getShortname(referencedClass)));
		
		return g;
	}
	
	private String getShortname(String className) {
		return StringUtils.replaceAll(className, interestedPackage, "*");
	}
	
	private String convertToDigraph(List<String> graph) throws NoGraphToAnalyzeException {
		
		if (graph.size() == 0) {
			throw new NoGraphToAnalyzeException();
		}
		
		List<String> classGraph = graph.stream()
				.filter(g -> g.startsWith("\"C:"))
				.collect(Collectors.toList());
		
		List<String> methodGraph = graph.stream()
				.filter(g -> !g.startsWith("\"C:"))
				.collect(Collectors.toList());
		
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {" + System.lineSeparator());
		sb.append("\trankdir=LR" + System.lineSeparator() + System.lineSeparator());
		
		sb.append(this.convertedSubgraph(classGraph, new ClassSubgraphTemplate(), "0"));
		
		sb.append(this.convertedSubgraph(methodGraph, new MethodSubgraphTemplate(),"1"));
		
		sb.append("}");
		
		return sb.toString();
	}
	
	private String convertToSubgraph(List<String> subgraph, String id, SubgraphTemplate template) {
		
		String toGraph = StringUtils.replace(
				StringUtils.join(subgraph, "\n\t"),
				"'",
				"\""
				);
		
		return String.format(
				template.template(),
				id,
				toGraph);
	}
	
	private String convertedSubgraph(List<String> g, SubgraphTemplate template, String title) {
		return g.size() > 0 ? this.convertToSubgraph(g, title, template) : "\n" ;
	}

}