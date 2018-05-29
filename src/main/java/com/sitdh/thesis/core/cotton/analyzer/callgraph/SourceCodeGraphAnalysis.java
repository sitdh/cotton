package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.io.IOException;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sitdh.thesis.core.cotton.analyzer.data.GraphVector;
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
import com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.repository.VectorRepository;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceCodeGraphAnalysis extends EmptyVisitor {
	
	public static final String DIGRAPH_CLASS_TYPE = "class";
	
	public static final String DIGRAPH_METHOD_TYPE = "method";
	
	private List<String> graphStructure;
	
	private List<Path> classListing;

	private Project project;
	
	@Getter
	private List<ConstantCollection> constantCollector;
	
	@Getter
	private List<ControlFlowGraph> controlFlowGraphs;

	@Getter
	private List<GraphVector> connections;
	
	@Setter
	private VectorRepository vectorRepo;

	public SourceCodeGraphAnalysis(
			List<Path> classListing, 
			Project project,
			VectorRepository vectorRepo) throws IOException {

		this.project = project;
		
		this.vectorRepo = vectorRepo;

		log.info("Object create");
		this.classListing = classListing;
		graphStructure = new ArrayList<String>();
		controlFlowGraphs = Lists.newArrayList();
		connections = Lists.newArrayList();
		constantCollector = Lists.newArrayList();
		
	}
	
	public SourceCodeGraphAnalysis analyze() throws ClassFormatException, IOException {
		for(Path path : this.classListing) {
			JavaClass jc = new ClassParser(path.toString()).parse();
			ClassStructureAnalysis csa = new ClassStructureAnalysis(jc, project, vectorRepo);
			csa.analyze();
			
			List<String> newGraph = csa.getStructure();

			graphStructure.addAll(newGraph);
			controlFlowGraphs.addAll(csa.getControlFlowGraphs());
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
		return StringUtils.replaceAll(className, this.project.getInterestedPackage(), "*");
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