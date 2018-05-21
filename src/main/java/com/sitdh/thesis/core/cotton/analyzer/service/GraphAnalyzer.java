package com.sitdh.thesis.core.cotton.analyzer.service;

import java.util.List;
import java.util.Optional;

public interface GraphAnalyzer extends Analyzer<List<String>> {

	public Optional<String> sourceLocation(String slug, String branch);
}
