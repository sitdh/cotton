package com.sitdh.thesis.core.cotton.analyzer.service.util;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassSubgraphTemplate implements SubgraphTemplate {

	@Override
	public String template() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("subgraph cluster_%s {" + System.lineSeparator());
		sb.append("\tstyle=filled" + System.lineSeparator());
		sb.append("\tcolor=lightgrey" + System.lineSeparator());
		sb.append("\tnode [style=filled,color=white];" + System.lineSeparator());
		sb.append("\t%s" + System.lineSeparator());
		sb.append("\tlabel = \"Classes\"" + System.lineSeparator());
		sb.append("}" + System.lineSeparator());
		
		log.debug("Template generated: " + sb.toString());
		
		return sb.toString();
	}

}
