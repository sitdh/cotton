package com.sitdh.thesis.core.cotton.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph;
import com.sitdh.thesis.core.cotton.database.entity.Project;

@Repository
public interface ControlFlowGraphRepository extends JpaRepository<ControlFlowGraph, Integer> {

	public List<ControlFlowGraph> findAllByProjectId(Project p);
	
}
