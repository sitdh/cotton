package com.sitdh.thesis.core.cotton.database.repository;

import java.util.List;

import org.evosuite.shaded.org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sitdh.thesis.core.cotton.database.entity.DynamicVector;
import com.sitdh.thesis.core.cotton.database.entity.Project;

@Repository
public interface DynamicVectorRepository extends JpaRepository<DynamicVector, Integer> {

	public List<DynamicVector> findAllByProject(Project project);
	
}
