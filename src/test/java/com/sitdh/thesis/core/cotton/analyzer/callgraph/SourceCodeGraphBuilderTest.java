package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sitdh.thesis.core.cotton.exception.PackageNotInterestedException;

public class SourceCodeGraphBuilderTest {
	
	public static final String PROJECT = "project-slug";
	
	public static final String BRANCH = "branch";
	
	public static final String HASH_STRING = "a-b-c-d-e";
	
	public static final String INTERESTED_PACKAGE = "com.sitdh.thesis.core.cotton";
	
	private SourceCodeGraphBuilder graphBuilder;
	
	@BeforeClass
	public static void setupEnvironment() {
		String tempdir = FileUtils.getTempDirectoryPath() + "graph-workspace";
		File f = new File(tempdir);
		f.deleteOnExit();
		(new File(tempdir)).mkdirs();
		
		String workspaceTemplate = tempdir 
				+ "/" 
				+ SourceCodeGraphBuilderTest.PROJECT 
				+ "_" 
				+ SourceCodeGraphBuilderTest.BRANCH 
				+ "-" 
				+ SourceCodeGraphBuilderTest.HASH_STRING;
		
		List<String> structureList = Arrays.asList(
				workspaceTemplate,
				workspaceTemplate + "/target/classes",
				workspaceTemplate + "/target/classes/A",
				workspaceTemplate + "/target/classes/B",
				workspaceTemplate + "/target/classes/B/C",
				workspaceTemplate + "@1",
				workspaceTemplate + "@2@temp",
				workspaceTemplate + "@3@temp"
				);
		
		structureList.forEach(dir -> {
			(new File(dir)).mkdirs();
		});
		
		List<String> classfile = Arrays.asList(
				workspaceTemplate + "/target/classes/1.class",
				workspaceTemplate + "/target/classes/A/A.class",
				workspaceTemplate + "/target/classes/B/B.class",
				workspaceTemplate + "/target/classes/B/C/C.class"
				);
		classfile.forEach(fileLocation -> {
			try {
				new File(fileLocation).createNewFile();
			} catch (IOException e) { e.printStackTrace(); }
		});
		
		System.setProperty("graph.app.jenkins.workspace-location", tempdir);
	}
	
	@Before
	public void setup() throws ClassFormatException, IOException, PackageNotInterestedException {
//		graphBuilder = SourceCodeGraphBuilder
//				.analyzedForProject("project-slug", "branch", "com.sitdh.thesis.core.cotton");
//		graphBuilder.analyze();
	}
	
	@Test
	public void classes_should_be_found() {
		assertNotNull(graphBuilder);
	}

}
