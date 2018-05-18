package com.sitdh.thesis.core.cotton.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sitdh.thesis.core.cotton.database.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

}
