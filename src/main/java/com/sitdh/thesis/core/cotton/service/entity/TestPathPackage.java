package com.sitdh.thesis.core.cotton.service.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class TestPathPackage {

	@Getter @Setter
	@JsonProperty(value="path_id")
	private String pathId;

	@Getter @Setter
	private List<String> steps;
	
}
