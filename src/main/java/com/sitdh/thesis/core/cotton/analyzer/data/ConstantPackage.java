package com.sitdh.thesis.core.cotton.analyzer.data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitdh.thesis.core.cotton.database.entity.constants.ConstantType;

public class ConstantPackage {

	@JsonProperty("file_name")
	public String filename;
	
	@JsonProperty("data_collection")
	public Map<ConstantType, List<String>> dataCollection;
}
