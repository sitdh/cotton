package com.sitdh.thesis.core.cotton.analyzer.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:/graph.properties")
public class GraphAnalyzerServiceFromExistingSourceTest {

	@Autowired
	@Qualifier("SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;
	
	@BeforeClass
	public static void setupInformationBeforeStart() {
		System.setProperty("graph.app.jenkins.workspace-location", "/Users/sitdh/Projects/thesis/fabric/jenkinsci/data/workspace");
	}

	@Test
	public void should_extract_real_class_data() throws NoGraphToAnalyzeException {
		Project p = mock(Project.class);
		when(p.getBranch()).thenReturn("master");
		when(p.getProjectId()).thenReturn("fibre-tax-income");
		when(p.getInterestedPackage()).thenReturn("com.sitdh.thesis.example");
		
		Map<String, String> location = graphAnalyzer.analyzedStructure(p);
		assertThat(!location.isEmpty(), is(true));
		System.out.println(location);
	}
}
