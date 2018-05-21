package com.sitdh.thesis.core.cotton.analyzer.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GraphAnalyzerServiceTest {
	
	@Autowired
	@Qualifier("SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;
	
	@Test
	public void should_read_directory() {
		assertNotNull(graphAnalyzer);
		Optional<String> location = graphAnalyzer.sourceLocation("fibre-tax-income", "master");
		assertTrue(location.isPresent());
		assertThat(location.get(), is(equalTo("fibre-tax-income_master")));
	}

}
