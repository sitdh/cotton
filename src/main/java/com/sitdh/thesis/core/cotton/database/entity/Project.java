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
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="container")
	private List<FileName> files;
	
	@Getter @Setter @Lob
	@Column(name="graph_graph", nullable=true)
	private String graphClass;
	
	@Getter @Setter @Lob
	@Column(name="graph_method", nullable=true)
	private String graphMethod;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="projectId")
	private List<ControlFlowGraph> graphs;
	
	public Project() { }
	
	public Project(String projectId) {
		this.setProjectId(projectId);
	}
}
