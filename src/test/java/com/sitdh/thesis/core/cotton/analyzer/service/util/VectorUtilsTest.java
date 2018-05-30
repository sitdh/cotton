package com.sitdh.thesis.core.cotton.analyzer.service.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.sitdh.thesis.core.cotton.database.entity.Vector;

public class VectorUtilsTest {
	
	private List<Vector> vectors;
	
	private List<Vector> completeVector;
	
	private List<Vector> duplicatedVector;
	
	@Before
	public void setup() {
		Vector v1 = mock(Vector.class);
		when(v1.getEdge()).thenReturn("s:m");
		when(v1.getSource()).thenReturn("com.sitdh.Sabaidee");
		when(v1.getTarget()).thenReturn("com.sitdh.Mingalaba");
		
		Vector v2 = mock(Vector.class);
		when(v2.getEdge()).thenReturn("main:s");
		when(v2.getSource()).thenReturn("com.sitdh.Aloha");
		when(v2.getTarget()).thenReturn("com.sitdh.Sabaidee");
		
		Vector v3 = mock(Vector.class);
		when(v3.getEdge()).thenReturn("s:s");
		when(v3.getSource()).thenReturn("com.sitdh.Sabaidee");
		when(v3.getTarget()).thenReturn("com.sitdh.Sawaddee");
		
		Vector v4 = mock(Vector.class);
		when(v4.getEdge()).thenReturn("s:s");
		when(v4.getSource()).thenReturn("com.sitdh.Sabaidee");
		when(v4.getTarget()).thenReturn("com.sitdh.Sabaidee");
		
		vectors = new ArrayList<Vector>();
		vectors.add(v1);
		vectors.add(v1);
		vectors.add(v2);
		
		completeVector = new ArrayList<Vector>();
		completeVector.add(v1);
		completeVector.add(v2);
		completeVector.add(v3);
		
		duplicatedVector = new ArrayList<Vector>();
		duplicatedVector.add(v4);
	}

	@Test
	public void cyclic_vector_should_removed() {
		List<Vector> v = VectorUtils.reduceCyclicPath(this.vectors);
		
		assertThat(v.size(), is(2));
		assertThat(v.get(0).getSource(), is("com.sitdh.Sabaidee"));
	}
	
	@Test
	public void main_class_should_be_detected() {
		Optional<Vector> v = VectorUtils.mainClass(this.completeVector);
		
		assertThat(v.isPresent(), is(true));
		assertThat(v.get().getSource(), is(containsString("com.sitdh.Aloha")));
		
		v = VectorUtils.mainClass(duplicatedVector);
		assertThat(v.isPresent(), is(false));
	}
	
	@Test
	public void detect_duplicated_vector() {
		boolean isDuplicated = VectorUtils.isDuplicate(duplicatedVector);
		assertThat(isDuplicated, is(true));
		
		isDuplicated = VectorUtils.isDuplicate(completeVector);
		assertThat(isDuplicated, is(false));
	}

}
