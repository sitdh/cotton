package com.sitdh.thesis.core.cotton.analyzer.service.util;

import org.springframework.stereotype.Service;

@Service
public class MethodSubgraphTemplate implements SubgraphTemplate {

	@Override
	public String template() {
		StringBuilder sb = new StringBuilder();
		sb.append("subgraph cluster_%s {" + System.lineSeparator());
		sb.append("\tnode [style=filled];" + System.lineSeparator());
		sb.append("\t%s" + System.lineSeparator());
		sb.append("\tlabel = \"Methods\"" + System.lineSeparator());
		sb.append("\tcolor=blue" + System.lineSeparator());
		sb.append("}" + System.lineSeparator());
		
		return sb.toString();
	}

}
