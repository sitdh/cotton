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
import org.assertj.core.util.Lists;

import com.sitdh.thesis.core.cotton.analyzer.service.util.ClassSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.MethodSubgraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.SubgraphTemplate;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceCodeGraphAnalysis extends EmptyVisitor {
	
	public static final String DIGRAPH_CLASS_TYPE = "class";
	
	public static final String DIGRAPH_METHOD_TYPE = "methods";
	
	private String interestedPackage;
	
	private List<String> graphStructure;
	
	private List<Path> classListing;

	public SourceCodeGraphAnalysis(List<Path> classListing, String interestedPackage) throws IOException {
		log.info("Object create");
		this.classListing = classListing;
		graphStructure = new ArrayList<String>();
		this.interestedPackage = interestedPackage;
	}
	
	
	/**
	 * @deprecated
	 * @param classListing
	 * @param interestedPackage
	 * @return
	 * @throws IOException
	 */
	public static SourceCodeGraphAnalysis analyzedForProject(List<Path> classListing, String interestedPackage) throws IOException {		
		return new SourceCodeGraphAnalysis(classListing, interestedPackage);
	}
	
	public static class SourceCodeGraphAnalysisBuilder {
		
		private List<Path> classListing;
		
		private String interestedPackage;
		
		public SourceCodeGraphAnalysisBuilder classListing(List<Path> classListing) {
			if (null == this.classListing) {
				this.classListing = Lists.emptyList();
			}
			
			this.classListing.addAll(classListing);
			
			return this;
		}
		
		public SourceCodeGraphAnalysisBuilder classPath(Path path) {
			if (null == this.classListing) {
				this.classListing = Lists.emptyList();
			}
			
			this.classListing.add(path);
			
			return this;
		}
		
		public SourceCodeGraphAnalysisBuilder interestedPackage(String interestedPackage) {
			this.interestedPackage = interestedPackage;
			
			return this;
		}
		
		public SourceCodeGraphAnalysis build() throws IOException {
			return new SourceCodeGraphAnalysis(classListing, interestedPackage);
		}
	}
	
	public SourceCodeGraphAnalysis analyze() throws ClassFormatException, IOException {
		
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