package com.sitdh.thesis.core.cotton.generator;

import lombok.Builder;

@Builder
public class TestcaseGeneraterProcessConfiguration implements ProcessConfiguration {
	
	private String javahome;
	
	private String evosuiteJar;
	
	private String projectLocation;

	@Override
	public String javahome() {
		return this.javahome;
	}

	@Override
	public String evosuiteJar() {
		return this.evosuiteJar;
	}

	@Override
	public String projectLocation() {
		return this.projectLocation;
	}

}
