package com.sitdh.thesis.core.cotton.database.entity.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConstantType {
	@JsonProperty("string")
	STRING,
	
	@JsonProperty("integer")
	INTEGER,
	
	@JsonProperty("float")
	FLOAT,
	
	@JsonProperty("decimal")
	DECIMAL;
}
