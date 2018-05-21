package com.sitdh.thesis.core.cotton.analyzer.service.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:/graph.properties")
public class LocationUtilsTest {
	
	private static final String PROJECT = "may-shihiro";
	
	private static final String BRANCH = "haku";
	
	private static final String HASH_STRING = "a-b-c-d-e-f-g";
	
	@Autowired
	private LocationUtils locationUtils;
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String projectLocation;
	
	@Value("${graph.app.project.classes}")
	private String classLocation;
	
	@BeforeClass
	public static void setupBeforeClass() throws IOException {
		String tempdir = FileUtils.getTempDirectoryPath() + "graph-workspace";
		File f = new File(tempdir);
		f.deleteOnExit();
		(new File(tempdir)).mkdirs();
		
		String workspaceTemplate = tempdir 
				+ "/" 
				+ LocationUtilsTest.PROJECT 
				+ "_" 
				+ LocationUtilsTest.BRANCH 
				+ "-" 
				+ LocationUtilsTest.HASH_STRING;
		
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
	public void graph_project_workspace_directory_should_exists() {
		String expectedTemp = FileUtils.getTempDirectoryPath() + "/graph-workspace";
		File f = new File(expectedTemp);
		assertThat(f.exists(), is(true));
		assertThat(f.isDirectory(), is(true));
	}
	
	@Test
	public void should_return_absolute_workspace_path() throws FileNotFoundException {
		String expectedLocation = String.format(
				"%s/%s_%s-%s", 
				projectLocation, 
				LocationUtilsTest.PROJECT,
				LocationUtilsTest.BRANCH,
				LocationUtilsTest.HASH_STRING);
				
		String location = locationUtils.getProjectFromWorkspace(
				LocationUtilsTest.PROJECT, 
				LocationUtilsTest.BRANCH);
		
		assertThat(location, is(expectedLocation));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void should_return_error_while_unknow_project_info() throws FileNotFoundException {
		locationUtils.getProjectFromWorkspace("unknow-project", "secret");
	}

}
