package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.io.IOException;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.lang3.StringUtils;

import com.sitdh.thesis.core.cotton.analyzer.service.util.ClassSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.MethodSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.SubgraphTemplate;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.extern.java.Log;

@Log
public class SourceCodeGraphBuilder extends EmptyVisitor {
	
	public static final String DIGRAPH_CLASS_TYPE = "class";
	
	public static final String DIGRAPH_METHOD_TYPE = "methods";
	
	private String interestedPackage;
	
	private List<String> graphStructure;
	
	private List<Path> classListing;

	private SourceCodeGraphBuilder(List<Path> classListing, String interestedPackage) throws IOException {
		log.info("Object create");
		this.classListing = classListing;
		graphStructure = new ArrayList<String>();
		this.interestedPackage = interestedPackage;
		
	}
	
	public static SourceCodeGraphBuilder analyzedForProject(List<Path> classListing, String interestedPackage) throws IOException {		
		return new SourceCodeGraphBuilder(classListing, interestedPackage);
	}
	
	public SourceCodeGraphBuilder analyze() throws ClassFormatException, IOException {
		
		for(Path path : this.classListing) {
			JavaClass jc = new ClassParser(path.toString()).parse();
			List<String> newGraph = ClassStructureAnalysis.forClass(jc, interestedPackage);
			graphStructure.addAll(newGraph);
		}
		
		return this;
	}
	
	public String getDigraph() throws NoGraphToAnalyzeException {
		return this.getShortname(
				this.convertToDigraph(graphStructure)
				);
	}
	
	private String getShortname(String className) {
		return StringUtils.replaceAll(className, interestedPackage, "*");
	}
	
	private String convertToDigraph(List<String> graph) throws NoGraphToAnalyzeException {
		
		if (graph.size() == 0) {
			throw new NoGraphToAnalyzeException();
		}
		
		graph = new ArrayList<>(new HashSet<>(graph));
		
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