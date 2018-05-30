package com.sitdh.thesis.core.cotton.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.apache.tinkerpop.shaded.minlog.Log;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;
import com.sitdh.thesis.core.cotton.controller.ErrorHandlerResponseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:/graph.properties")
public class TestcaseGeneratorProcessBuilder implements TestCaseBuilder {
	
	private String projectLocation;
	
	@Autowired
	private LocationUtils locationUtils;

	@Override
	public void build(ProcessConfiguration processConfig) throws IOException, InterruptedException {
		Log.debug("Getting start to build command");
		List<String> command = Lists.newArrayList(
				"/Library/Java/JavaVirtualMachines/jdk1.8.0_144.jdk/Contents/Home/bin/java",
				"-jar",
				"/Users/sitdh/Desktop/unit-test/evosuite-1.0.6.jar",
				"-target",
				"target/classes",
				"-criterion",
				"branch"
				);
		
		log.debug("Command builded: " + command.toString());
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File("/Users/sitdh/Projects/thesis/fabric/jenkinsci/data/workspace/fibre-grading-demo_master-YGWIODUGNELJKFWA5VCROPQRWZG4AUCAFFA2QLK2MSOOESH6VWJQ"));
		builder.command(command);
		
		builder.redirectOutput(Redirect.INHERIT);
		builder.redirectError(Redirect.INHERIT);
		
		Process process = builder.start();
				
		int exitCode = process.waitFor();
		
		assert exitCode == 0;
		
	}

}
