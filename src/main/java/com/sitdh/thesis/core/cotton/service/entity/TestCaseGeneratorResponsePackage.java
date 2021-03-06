package com.sitdh.thesis.core.cotton.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class TestCaseGeneratorResponsePackage {
	
	@Getter @Setter @JsonProperty(value="message")
	private String testmessage;
	
	@Getter @Setter @JsonProperty(value="is_generated")
	private String generated;

}
