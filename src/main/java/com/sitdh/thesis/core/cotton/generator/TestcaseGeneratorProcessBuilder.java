package com.sitdh.thesis.core.cotton.generator;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.apache.tinkerpop.shaded.minlog.Log;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TestcaseGeneratorProcessBuilder implements TestCaseBuilder {

	@Override
	public void build(ProcessConfiguration processConfig) throws IOException, InterruptedException {
		Log.debug("Getting start to build command");
		List<String> command = Lists.newArrayList(
				processConfig.javahome(),
				"-jar",
				processConfig.evosuiteJar(),
				"-target",
				"target/classes",
				"-criterion",
				"branch"
				);
		
		log.debug("Command builded: " + command.toString());
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(processConfig.projectLocation()));
		builder.command(command);
		
		builder.redirectOutput(Redirect.INHERIT);
		builder.redirectError(Redirect.INHERIT);
		
		Process process = builder.start();
		int exitCode = 0;
		exitCode = process.waitFor();
		
		assert exitCode == 0;
		
	}

}
