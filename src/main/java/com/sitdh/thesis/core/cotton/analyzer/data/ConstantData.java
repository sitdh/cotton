package com.sitdh.thesis.core.cotton.analyzer.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class ConstantData {

	@Getter @Setter
	@JsonProperty("file_name")
	private String filename;
	
	@Getter @Setter
	@JsonProperty("collection")
	private List<ConstantsList> dataCollection;
	
	public ConstantData(String filename, List<ConstantsList> collection) {
		this.filename = filename;
		this.setDataCollection(collection);
	}
}
