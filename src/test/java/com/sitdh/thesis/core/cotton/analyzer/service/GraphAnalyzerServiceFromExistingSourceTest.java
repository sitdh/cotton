package com.sitdh.thesis.core.cotton.analyzer.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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
		
		Map<String, String> location = graphAnalyzer.analyzedStructure("fibre-tax-income", "master", "com.sitdh.thesis.example");
		assertThat(!location.isEmpty(), is(true));
		System.out.println(location);
	}
}
