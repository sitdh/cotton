package com.sitdh.thesis.core.cotton.analyzer.service.util;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlainGraphTemplate implements SubgraphTemplate {

	@Override
	public String template() {
		StringBuilder sb = new StringBuilder();
		sb.append("label = \"%s\";" + System.lineSeparator());
		sb.append("%s" + System.lineSeparator());
		
		log.debug("Template generated: " + sb.toString());
		
		return sb.toString();
	}

}
