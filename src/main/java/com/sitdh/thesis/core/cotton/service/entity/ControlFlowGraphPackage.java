package com.sitdh.thesis.core.cotton.service.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph;

import lombok.Getter;
import lombok.Setter;

public class ControlFlowGraphPackage {

	@Getter @Setter
	@JsonProperty(value="class_name")
	private String className;
	
	@Getter @Setter
	@JsonProperty(value="cfg_package")
	private List<MethodControlFlowGraphPackage> methodName;
	
	public ControlFlowGraphPackage(String className, List<ControlFlowGraph> c) {
		this.className = className;
		this.methodName = Lists.newArrayList();
		
		c.stream().forEach(mcfg -> {
			MethodControlFlowGraphPackage m = new MethodControlFlowGraphPackage(mcfg.getCgId().getMethodName(), mcfg.getGraph());
			methodName.add(m);
		});
	}
}
