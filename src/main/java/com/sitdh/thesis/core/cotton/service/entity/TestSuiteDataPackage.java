package com.sitdh.thesis.core.cotton.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class TestSuiteDataPackage {
	
	@Getter @Setter @JsonProperty(value="class_name")
	private String classname;
	
	@Getter @Setter @JsonProperty(value="package_name")
	private String packagename;
	
	@Getter @Setter @JsonProperty(value="test_case")
	private String testcase;

}
