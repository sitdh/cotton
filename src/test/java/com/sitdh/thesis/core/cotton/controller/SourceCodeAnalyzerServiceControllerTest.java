package com.sitdh.thesis.core.cotton.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

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
import com.sitdh.thesis.core.cotton.analyzer.service.ConstantAnalyzerService;
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
	
	@MockBean(name="SimpleCollector")
	private ConstantAnalyzerService simpleCollector;

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
					content().string(
							"[{\"file_name\":\"LoremIpsum.java\","
							+ "\"collection\":[{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]},"
							+ "{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]}]},"
							+ "{\"file_name\":\"DolorSitAmet.java\","
							+ "\"collection\":[{\"type\":\"string\",\"values\":[\"A\",\"B\",\"C\"]}]}]"));
		
	}
	
	@Test
	public void shouldReturnGraphStructure() throws Exception {
		
		this.mockMvc
			.perform(get("/code/graph"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string("{\"B\":\"C\",\"C\":\"D\",\"A\":\"B\"}"));
	}

}
