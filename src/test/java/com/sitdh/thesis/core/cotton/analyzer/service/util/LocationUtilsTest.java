package com.sitdh.thesis.core.cotton.analyzer.service.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
	

	@Autowired
	private LocationUtils locationUtils;
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String projectLocation;
	
	@Value("${graph.app.project.classes}")
	private String classLocation;
	
	private static final String PROJECT = "may-shihiro";
	
	private static final String BRANCH = "haku";
	
	private static final String HASH_STRING = "a-b-c-d-e-f-g";
	
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
		
		(new File(workspaceTemplate + "/A.class")).createNewFile();
		(new File(workspaceTemplate + "/B.class")).createNewFile();
		(new File(workspaceTemplate + "/C.class")).createNewFile();
		
		(new File(workspaceTemplate + "/A")).mkdirs();
		(new File(workspaceTemplate + "/A/A1.class")).createNewFile();
		
		(new File(workspaceTemplate + "/A/A1HELLO")).createNewFile();
		
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
				
		String location = locationUtils.getProjectWorkspace(
				LocationUtilsTest.PROJECT, 
				LocationUtilsTest.BRANCH);
		
		assertThat(location, is(expectedLocation));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void should_return_error_while_unknow_project_info() throws FileNotFoundException {
		locationUtils.getProjectWorkspace("unknow-project", "secret");
	}
	
	@Test
	public void should_list_all_class_file_from_project_workspace() throws IOException {
		List<Path> classfileLocations = locationUtils.listClassFiles(LocationUtilsTest.PROJECT, LocationUtilsTest.BRANCH);
		assertThat(classfileLocations.size() == 4, is(true));
		classfileLocations.forEach(p -> {
			assertThat(p.toString().endsWith("class"), is(true));
		});
	}
	
	@Test
	public void should_read_all_listed_class() throws IOException {
		List<Path> classfileLocations = locationUtils.listClassFiles(LocationUtilsTest.PROJECT, LocationUtilsTest.BRANCH);
		
		for(Path p : classfileLocations) {
			File f = new File(p.toAbsolutePath().toString());
			assertThat(f.exists(), is(true));
		}
		
	}
	
	@Test(expected = FileNotFoundException.class)
	public void should_not_return_any_files_from_unknonw_project() throws IOException {
		List<Path> classfileLocation = locationUtils.listClassFiles("unknown-prject", "secret");
		assertThat(classfileLocation.size() == 0, is(true));
	}

    @Test
    public void getProjectWorkspace() {
    }

    @Test
    public void listClassFiles() {
    }

    @Test
    public void getMainClass() {
    }

    @Test
    public void filterForMainClass() {
    }
}
