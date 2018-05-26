package com.sitdh.thesis.core.cotton.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sitdh.thesis.core.cotton.analyzer.data.ConstantData;
import com.sitdh.thesis.core.cotton.analyzer.data.ConstantsList;
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzer;
import com.sitdh.thesis.core.cotton.analyzer.service.GraphAnalyzer;
import com.sitdh.thesis.core.cotton.database.entity.constants.ConstantType;
import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

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
	
	@Autowired
	private SourceCodeAnalyzerServiceController srcAnalyzer;

//	@Test
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
	
	@Test
	public void should_not_provide_for_empty_variable() throws Exception {
		this.mockMvc
			.perform(get("/code/graph"))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andExpect(content().string("Not found"));
	}
	
	@Test
	public void should_return_graph_structure_directly_from_controller() throws NoGraphToAnalyzeException {
		when(graphAnalyzer.analyzed(anyString(), anyString(), anyString())).thenReturn("digraph G { subgraph {} }");
		
		ResponseEntity<Map<String, String>> response = srcAnalyzer.analyzeSourcecodeForGraph("fibre-tax-income", "master", "com.sitdh.thesis.example");
		
		assertNotNull(response.getBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void should_return_graph_structure() throws Exception {
		
		when(graphAnalyzer.analyzed(anyString(), anyString(), anyString())).thenReturn("digraph G { subgraph {} }");
		
		this.mockMvc
			.perform(get("/code/graph/fibre-grading-demo/master/?p=com.sitdh.thesis.demo"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("subgraph")));
	}

}
