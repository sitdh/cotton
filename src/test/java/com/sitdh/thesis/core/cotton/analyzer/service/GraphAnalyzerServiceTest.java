package com.sitdh.thesis.core.cotton.analyzer.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerServiceTest {
	
	@Autowired
	@Qualifier("SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String projectLocation;
	
	private static final String PROJECT = "may-shihiro";
	
	private static final String BRANCH = "haku";
	
	private static final String HASH_STRING = "a-b-c-d-e-f-g";
	
	String expectedLocation = String.format(
			"%s/%s_%s-%s", 
			projectLocation, 
			GraphAnalyzerServiceTest.PROJECT,
			GraphAnalyzerServiceTest.BRANCH,
			GraphAnalyzerServiceTest.HASH_STRING);
	
	@BeforeClass
	public static void setupBeforeClass() throws IOException {
		String tempdir = FileUtils.getTempDirectoryPath() + "graph-workspace";
		File f = new File(tempdir);
		f.deleteOnExit();
		(new File(tempdir)).mkdirs();
		
		String workspaceTemplate = tempdir 
				+ "/" 
				+ GraphAnalyzerServiceTest.PROJECT 
				+ "_" 
				+ GraphAnalyzerServiceTest.BRANCH 
				+ "-" 
				+ GraphAnalyzerServiceTest.HASH_STRING;
		
		List<String> structureList = Arrays.asList(
				workspaceTemplate,
				workspaceTemplate + "/target/classes",
				workspaceTemplate + "@1",
				workspaceTemplate + "@2@temp",
				workspaceTemplate + "@3@temp"
				);
		
		structureList.forEach(dir -> {
			(new File(dir)).mkdirs();
		});
		
		System.setProperty("graph.app.jenkins.workspace-location", tempdir);
	}
	
	@Test
	public void should_read_directory() {
		Optional<String> location = graphAnalyzer.sourceLocation(GraphAnalyzerServiceTest.PROJECT, GraphAnalyzerServiceTest.BRANCH);
		assertTrue(location.isPresent());
		assertThat(location.get(), is(equalTo(expectedLocation)));
	}

}
