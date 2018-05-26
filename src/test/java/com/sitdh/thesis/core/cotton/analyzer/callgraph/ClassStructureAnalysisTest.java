package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassStructureAnalysisTest {
	
	@Value("${class-loation}")
	private String location;

	@BeforeClass
	public static void setupEnvironment() {
		System.setProperty("class-loation", "/Users/sitdh/workspace/grading/target/classes/com/sitdh/thesis/demo/GradingApplication.class");
	}

	@Test
	public void system_property_should_assigned() {
		assertThat(location.isEmpty(), is(false));
		assertThat(location.startsWith("/Users/sitdh/workspace/"), is(true));
	}
	
	@Test
	public void class_should_extract_structure_from_list() throws ClassFormatException, IOException {
		JavaClass jc = new ClassParser(location).parse();
		List<String> structure = ClassStructureAnalysis.forClass(jc, "com.sitdh.thesis.demo");
		
		assertThat(structure.size() > 0, is(true));
	}
}
