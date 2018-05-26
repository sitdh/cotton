package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.io.IOException;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sitdh.thesis.core.cotton.analyzer.service.util.PlainGraphTemplate;
import com.sitdh.thesis.core.cotton.analyzer.service.util.SubgraphTemplate;
import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceCodeGraphAnalysis extends EmptyVisitor {
	
	public static final String DIGRAPH_CLASS_TYPE = "class";
	
	public static final String DIGRAPH_METHOD_TYPE = "method";
	
	private String interestedPackage;
	
	private List<String> graphStructure;
	
	private List<Path> classListing;
	
	@Getter
	private List<ConstantCollection> constantCollector;

	public SourceCodeGraphAnalysis(List<Path> classListing, String interestedPackage) throws IOException {
		log.info("Object create");
		this.classListing = classListing;
		graphStructure = new ArrayList<String>();
		this.interestedPackage = interestedPackage;
		
		constantCollector = Lists.newArrayList();
	}
	
	public static class SourceCodeGraphAnalysisBuilder {
		
		private List<Path> classListing;
		
		private String interestedPackage;
		
		public SourceCodeGraphAnalysisBuilder classListing(List<Path> classListing) {
			if (null == this.classListing) {
				this.classListing = new ArrayList<>();
			}
			
			this.classListing.addAll(classListing);
			
			return this;
		}
		
		public SourceCodeGraphAnalysisBuilder classPath(Path path) {
			if (null == this.classListing) {
				this.classListing = new ArrayList<>();
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
			ClassStructureAnalysis csa = ClassStructureAnalysis.forClass(jc, interestedPackage);
			List<String> newGraph = csa.getStructure();
			constantCollector.addAll(csa.getConstantsCollection());
			graphStructure.addAll(newGraph);
		}
		
		return this;
	}
	
	public Map<String, String> getGraphs() throws NoGraphToAnalyzeException {
		List<String> graph = null;
		Map<String, String> graphs = Maps.newHashMap();
		
		graph = graphStructure.stream()
				.filter(g -> g.startsWith("\"C:"))
				.filter(this::filterLeftEqualRight)
				.collect(Collectors.toList());
		String convertedGraph = this.convertToDotFile("Classes", graph);
		graphs.put(DIGRAPH_CLASS_TYPE, this.getShortname(convertedGraph));
		log.debug("Clases were converted to : ", this.getShortname(convertedGraph));
		
	
		graph = graphStructure.stream()
				.filter(g -> !g.startsWith("\"C:"))
				.collect(Collectors.toList());
		convertedGraph = this.convertToDotFile("Method", graph);
		graphs.put(DIGRAPH_METHOD_TYPE, this.getShortname(convertedGraph));
		log.debug("Methods were converted to ", this.getShortname(convertedGraph));
		
		return graphs;
	}
	
	private String getShortname(String className) {
		return StringUtils.replaceAll(className, interestedPackage, "*");
	}
	
	private String convertToDotFile(String graphName, List<String> graph) throws NoGraphToAnalyzeException {
		if (graph.size() == 0) {
			throw new NoGraphToAnalyzeException();
		}
		
		SubgraphTemplate plainGraphTemplate = new PlainGraphTemplate();
		
		graph = new ArrayList<>(new HashSet<>(graph));
		
		StringBuilder sb = new StringBuilder();
		sb.append("digraph " + graphName + " {" + System.lineSeparator());
		sb.append("\trankdir=LR; " + System.lineSeparator() + System.lineSeparator());
		
		sb.append(this.convertedSubgraph(graph, plainGraphTemplate, graphName));
		
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
	
	private boolean filterLeftEqualRight(String graph) {
		String[] str = graph.trim().split(" -> ");
		return !(str[0] + ";").equals(str[1]);
	}

}