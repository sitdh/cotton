package com.sitdh.thesis.core.cotton.generator;

import java.io.IOException;

public interface TestCaseBuilder {

	public void build(ProcessConfiguration processConfig) throws IOException, InterruptedException;
	
}
