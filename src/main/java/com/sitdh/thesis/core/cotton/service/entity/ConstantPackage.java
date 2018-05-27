package com.sitdh.thesis.core.cotton.service.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class ConstantPackage {

	@Getter @Setter
	@JsonProperty(value="type")
	private String constantType;
	
	@Getter @Setter
	private List<String> constants;
	
	public ConstantPackage() { }
	
	public ConstantPackage(String constantType, List<String> constants) {
		this.setConstantType(constantType);
		this.setConstants(constants);
	}
}
