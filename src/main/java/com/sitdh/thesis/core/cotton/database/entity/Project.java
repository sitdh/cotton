package com.sitdh.thesis.core.cotton.database.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class Project {

	@Getter @Setter
	@Id @Column(name="project_id", length=50)
	private String projectId;
	
	@Getter @Setter
	@Column(name="owner", length=50, nullable=true)
	private String owner;
	
	@Getter @Setter
	@Column(length=50)
	private String branch;
	
	@Getter @Setter
	@Column(name="interested_package", length=50)
	private String interestedPackage;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="container")
	private List<FileName> files;
	
	@Getter @Setter @Lob
	@Column(name="graph_class", nullable=true)
	private String graphClass;
	
	@Getter @Setter @Lob
	@Column(name="graph_method", nullable=true)
	private String graphMethod;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="projectId")
	private List<ControlFlowGraph> graphs;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="project")
	private List<Vector> vectors;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="project")
	private List<TestPath> testpaths;
	
	@Getter @Setter 
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="project")
	private List<TestSuite> testsuites;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="project")
	private List<DynamicVector> dynamicVectors;
	
	public Project() { }
	
	public Project(String projectId) {
		this.setProjectId(projectId);
	}
	
	public Project(String projectId, String branch, String interestedPackage) {
		this.setProjectId(projectId);
		this.setBranch(branch);
		this.setInterestedPackage(interestedPackage);
	}
}
