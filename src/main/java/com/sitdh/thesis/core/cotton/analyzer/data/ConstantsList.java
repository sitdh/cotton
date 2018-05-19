package com.sitdh.thesis.core.cotton.analyzer.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitdh.thesis.core.cotton.database.entity.constants.ConstantType;

import lombok.Getter;
import lombok.Setter;

public class ConstantsList {

	@Getter @Setter
	@JsonProperty("type")
	private ConstantType type;
	
	@Getter @Setter
	@JsonProperty("values")
	private List<String> values;
	
	public ConstantsList(ConstantType type, List<String> values) {
		this.setType(type);
		this.setValues(values);
	}
}
