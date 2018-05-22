package com.sitdh.thesis.core.cotton.analyzer.service;

import java.util.List;
import java.util.Optional;

import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

public interface GraphAnalyzer extends Analyzer<List<String>> {

	public Optional<String> sourceLocation(String slug, String branch);
	
	public String analyzed(String slug, String brunch, String interestedPackage) throws NoGraphToAnalyzeException;
}
