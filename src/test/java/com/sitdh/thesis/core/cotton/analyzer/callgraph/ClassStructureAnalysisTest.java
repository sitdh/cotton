package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Test;

public class ClassStructureAnalysisTest {
	
	private String location;

	@Before
	public void setupEnvironment() {
		location = "/Users/sitdh/workspace/grading/target/classes/com/sitdh/thesis/demo/GradingApplication.class";
	}

	@Test
	public void system_property_should_assigned() {
		assertThat(location.isEmpty(), is(false));
		assertThat(location.startsWith("/Users/sitdh/workspace/"), is(true));
	}
	
	@Test
	public void class_should_extract_structure_from_list() throws ClassFormatException, IOException {
		JavaClass jc = new ClassParser(location).parse();
		List<String> structure = ClassStructureAnalysis.forClass(jc, "com.sitdh.thesis.demo").getStructure();
		
		assertThat(structure.size() > 0, is(true));
	}
}
