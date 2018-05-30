package com.sitdh.thesis.core.cotton.analyzer.service.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.database.entity.Vector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VectorUtils {

	public List<Vector> cyclicVectorRemove(List<Vector> vectors) {
		
		
		return null;
	}

	public static List<Vector> reduceCyclicPath(List<Vector> vectors) {
		
		List<Vector> gvs = Lists.newArrayList();
		vectors.stream().forEach(v -> {
			if (gvs.stream().noneMatch(gv -> gv.equals(v))) 
				gvs.add(v);
		});
		
		return gvs;
	}
	
	public static Optional<List<Vector>> allMainClass(List<Vector> vectors) {
		List<Vector> vs = vectors.stream()
				.filter(VectorUtils::mainEdgeDetection)
				.collect(Collectors.toList());
		
		if (0 == vs.size()) vs = null;
		
		return Optional.ofNullable(vs);
	}
	
	public static Optional<Vector> mainClass(List<Vector> vectors) {
		Vector vx = null;
		
		if (VectorUtils.allMainClass(vectors).isPresent()) {
			vx = VectorUtils.allMainClass(vectors).get().get(0);
		}
		
		return Optional.ofNullable(vx);
	}

	public static boolean isDuplicate(List<Vector> vectors) {
		log.debug("Filter vector");
		
		return vectors.stream()
				.filter(VectorUtils::dulicatedVectorFilter)
				.collect(Collectors.toList()).size() > 0;
	}
	
	public static void allTestPath(List<Vector> vectors) {
//		TinkerGraph graph = TinkerGraph.open();
//		GraphTraversalSource g = graph.traversal();
//		
//		Optional<List<Vector>> queriedMainClass = VectorUtils.allMainClass(vectors);
//		if (queriedMainClass.isPresent()) {
//			List<Vector> mainClasses = queriedMainClass.get();
//		}
		
	}
	
	//
	// ----------------------- Private -----------------------
	//
	
	private static boolean dulicatedVectorFilter(Vector v) {
		return v.getSource().equals(v.getTarget());
	}
	
	private static boolean mainEdgeDetection(Vector v) {
		return StringUtils.startsWith(v.getEdge(), "main");
	}
	
}
