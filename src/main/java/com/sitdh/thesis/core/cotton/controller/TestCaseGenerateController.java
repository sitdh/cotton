package com.sitdh.thesis.core.cotton.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.analyzer.service.util.LocationUtils;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.repository.ProjectRepository;
import com.sitdh.thesis.core.cotton.generator.ProcessConfiguration;
import com.sitdh.thesis.core.cotton.generator.TestCaseBuilder;
import com.sitdh.thesis.core.cotton.generator.TestcaseGeneraterProcessConfiguration;
import com.sitdh.thesis.core.cotton.service.entity.TestCaseGeneratorResponsePackage;
import com.sitdh.thesis.core.cotton.service.entity.TestSuiteDataPackage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@PropertySource("classpath:/graph.properties")
public class TestCaseGenerateController {
	
	@Value("${graph.sdk.tool.java.home}")
	private String defaultJavaLocation;
	
	@Value("${graph.sdk.tool.evosuite}")
	private String defaultEvosuiteLib;
	
	@Autowired
	private HttpHeaders headers;
	
	private ProjectRepository projectRepo;
	
	private LocationUtils locationUtils;
	
	private TestCaseBuilder testBuilder;
	
	@Autowired
	public TestCaseGenerateController(
			ProjectRepository projectRepo,
			LocationUtils locationUtils,
			TestCaseBuilder testBuilder
			) {
		
		this.projectRepo = projectRepo;
		this.locationUtils = locationUtils;
		this.testBuilder = testBuilder;
	}
	
	@GetMapping("/test/generator/{slug}")
	public ResponseEntity<TestCaseGeneratorResponsePackage> generateTestCaseForProject(@PathVariable String slug) throws FileNotFoundException {
		log.debug("Getting start");
		
		TestCaseGeneratorResponsePackage tcgPackage = new TestCaseGeneratorResponsePackage();
		tcgPackage.setGenerated("Failure");
		
		Optional<Project> project = projectRepo.findById(slug);
		if (project.isPresent()) {
			String projectLocation = locationUtils.getProjectWorkspace(project.get());
			 
			boolean fileExists = false;
			
			ProcessConfiguration pconf = TestcaseGeneraterProcessConfiguration.builder()
					.javahome(this.defaultJavaLocation)
					.evosuiteJar(this.defaultEvosuiteLib)
					.projectLocation(projectLocation)
					.build();
			
			try {
				this.testBuilder.build(pconf);
				
				fileExists = new File(projectLocation + "/evosuite-tests").exists();
				
				tcgPackage.setTestmessage("Fine");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				tcgPackage.setTestmessage("Failure");
			}
			
			String testcaseCreated = fileExists ? "Done" : "Failure" ;
			
			tcgPackage.setGenerated(testcaseCreated);
			
		} else {
			tcgPackage.setTestmessage("Project not found");
		}
		
		
		
		return new ResponseEntity<>(tcgPackage, headers, HttpStatus.OK);
	}
	
	@GetMapping("/test/testsuite/{slug}")
	public ResponseEntity<List<TestSuiteDataPackage>> testsuiteProvider(@PathVariable String slug) throws FileNotFoundException {
		Optional<Project> optionalProject = projectRepo.findById(slug);
		List<TestSuiteDataPackage> testsuitePackage = Lists.newArrayList();
		List<File> testsuiteFile = Lists.newArrayList();
		if (optionalProject.isPresent()) {
			Project project = optionalProject.get();
			testsuiteFile = locationUtils.getEvosuteTestFiles(project);
			testsuiteFile.forEach(file -> {
				String content = "";
				String packagename = "";
				String classname = "";
				
				try {
					content = FileUtils.readFileToString(file, Charset.defaultCharset());
					
					packagename = FileUtils.readLines(file, Charset.defaultCharset())
							.stream()
							.filter(c -> c.startsWith("package"))
							.map(c -> StringUtils.replace(c, "package ", ""))
							.map(c -> StringUtils.replace(c, ";", ""))
							.peek(c -> log.debug("Package name: " + c))
							.findFirst()
							.orElse(project.getInterestedPackage());
					
					classname = file.getName().replaceAll(".java", "");
					
					testsuitePackage.add(TestSuiteDataPackage.builder()
							.classname(classname)
							.packagename(packagename)
							.testcase(content)
							.build());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			});
		}
		
		return new ResponseEntity<>(testsuitePackage, headers, HttpStatus.OK);
	}

}
