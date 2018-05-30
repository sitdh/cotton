package com.sitdh.thesis.core.cotton.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.entity.TestPath;

@Repository
public interface TestPathRepository extends JpaRepository<TestPath, String>{

	public List<TestPath> findAllByProject(Project project);
	
}
