package com.sitdh.thesis.core.cotton.analyzer.data;

import org.apache.bcel.Const;

import lombok.Getter;

public enum InterestedConstant {
	CONSTANT_CLASS(Const.CONSTANT_Class, "class"),
	CONSTANT_DOUBLE(Const.CONSTANT_Double, "double"),
	CONSTANT_FLOAT(Const.CONSTANT_Float, "float"),
	CONSTANT_INTEGER(Const.CONSTANT_Integer, "integer"),
	CONSTANT_STRING(Const.CONSTANT_String, "string"),
	UNKNOWN_CONSTANT( (byte) -1, "");
	
	@Getter
	private final byte constantType;
	
	@Getter
	private final String type;

	InterestedConstant(byte constantTag, String type) {
		this.constantType = constantTag;
		this.type = type;
	}
	
	public static InterestedConstant getInterestedConstant(byte type) {
		InterestedConstant constantType = InterestedConstant.UNKNOWN_CONSTANT;
		
		switch(type) {
		case Const.CONSTANT_Class: 
			constantType = InterestedConstant.CONSTANT_CLASS; 
			break;
			
		case Const.CONSTANT_Double: 
			constantType = InterestedConstant.CONSTANT_DOUBLE; 
			break;
			
		case Const.CONSTANT_Float: 
			constantType = InterestedConstant.CONSTANT_FLOAT; 
			break;
			
		case Const.CONSTANT_Integer: 
			constantType = InterestedConstant.CONSTANT_INTEGER; 
			break;
			
		case Const.CONSTANT_String: 
			constantType = InterestedConstant.CONSTANT_STRING; 
			break;
		
		}
		
		return constantType;
	}
	
	public String toString() {
		return String.format("Type: %s, Value: %s", this.type, this.constantType);
	}
}
