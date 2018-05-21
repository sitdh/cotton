package com.sitdh.thesis.core.cotton.analyzer.callgraph;

public enum SourcecodeStructureConnectionType {
	INVOKEVIRTUAL("M"),
	INVOKEINTERFACE("I"),
	INVOKESPECIAL("O"),
	INVOKESTATIC("S"),
	INVOKEDYNAMIC("D");
	
	private final String type;
	
	SourcecodeStructureConnectionType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}
}
