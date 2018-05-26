package com.sitdh.thesis.core.cotton.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;

public interface ConstantCollectionRepository extends JpaRepository<ConstantCollection, String> {

	public List<ConstantCollection> findByProjectId(String projectId);
	
}
