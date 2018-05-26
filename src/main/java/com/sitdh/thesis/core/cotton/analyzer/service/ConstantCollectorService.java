package com.sitdh.thesis.core.cotton.analyzer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sitdh.thesis.core.cotton.analyzer.data.ConstantData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("SimpleConstantsCollectorAnalyzer")
public class ConstantCollectorService implements ConstantAnalyzer {

	@Override
	public List<ConstantData> analyzed() {
		log.info("Analyzed");
		return null;
	}

}
