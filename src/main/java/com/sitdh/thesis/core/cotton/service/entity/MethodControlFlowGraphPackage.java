package com.sitdh.thesis.core.cotton.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class MethodControlFlowGraphPackage {

	@Getter @Setter
	@JsonProperty(value="method_name")
	private String methodName;

	@Getter @Setter
	@JsonProperty
	private String graph;
	
	public MethodControlFlowGraphPackage() { }
	
	public MethodControlFlowGraphPackage(String methodName, String graph) {
		this.methodName = methodName;
		this.graph = graph;
	}
}
