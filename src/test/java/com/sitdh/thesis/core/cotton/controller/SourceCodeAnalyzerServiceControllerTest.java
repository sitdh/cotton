package com.sitdh.thesis.core.cotton.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sitdh.thesis.core.cotton.analyzer.data.ConstantData;
import com.sitdh.thesis.core.cotton.analyzer.data.ConstantsList;
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;
import com.sitdh.thesis.core.cotton.analyzer.service.GraphAnalyzer;
import com.sitdh.thesis.core.cotton.database.entity.constants.ConstantType;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SourceCodeAnalyzerServiceControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	ConstantsList stringList, floatList;
	
	@Before
	public void setup() {
		stringList = new ConstantsList(ConstantType.STRING, Arrays.asList("A", "B", "C"));
		floatList = new ConstantsList(ConstantType.STRING, Arrays.asList("A", "B", "C"));
	}
	
	@MockBean(name="SimpleConstantsCollectorAnalyzer")
	private ConstantAnalyzer simpleCollector;
	
	@MockBean(name="SimpleGraphAnalyzer")
	private GraphAnalyzer graphAnalyzer;

	@Test
	public void shouldReturnConsntantList() throws Exception {
		String filename = "LoremIpsum.java";
		ConstantData packageFile = new ConstantData(filename, Arrays.asList(stringList, floatList));
		ConstantData anotherFile = new ConstantData("DolorSitAmet.java", Arrays.asList(stringList));
		
		when(simpleCollector.analyzed()).thenReturn(Arrays.asList(packageFile, anotherFile));
		
		this.mockMvc
			.perform(get("/code/constant"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(
					content().json(
							"[{\"file_name\":\"LoremIpsum.java\","
							+ "\"collection\":[{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]},"
							+ "{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]}]},"
							+ "{\"file_name\":\"DolorSitAmet.java\","
							+ "\"collection\":[{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]}]}]"));
		
	}
	
	public void should_not_provide_for_empty_variable() throws Exception {
		this.mockMvc
			.perform(get("/code/graph"))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andExpect(content().string("Not found"));
	}
	
	@Test
	public void should_return_graph_structure() throws Exception {
		
		List<String> graphInfo = Arrays.asList(
				"'C:*.TaxableApplication' -> 'C:*.navigation.ApplicationNavigator' [label = 'methodA():methodB()'];",
				"'C:*.TaxableApplication' -> 'C:*.runnable.ApplicationRunnable' [label = 'methodA():methodB()'];",
				"'C:*.TaxableApplication' -> 'C:*.TaxableApplication';");
		
		when(graphAnalyzer.analyzed()).thenReturn(graphInfo);
		
		this.mockMvc
			.perform(get("/code/graph/ab/bb"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().json("[\"'C:*.TaxableApplication' -> 'C:*.navigation.ApplicationNavigator' [label = 'methodA():methodB()'];\","
					+ "\"'C:*.TaxableApplication' -> 'C:*.runnable.ApplicationRunnable' [label = 'methodA():methodB()'];\","
					+ "\"'C:*.TaxableApplication' -> 'C:*.TaxableApplication';\"]"));
	}

}
