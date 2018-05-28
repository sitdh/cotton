package com.sitdh.thesis.core.cotton.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="control_flow_graph")
public class ControlFlowGraph {

	@Column(name="cg_id") @Getter @Setter
	@EmbeddedId
	private ControlFlowGraphIdentity cgId;
	
	@Getter @Setter
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="projectId") @JsonIgnore
	private Project projectId;

	@Getter @Setter @Lob
	@Column(name="graph")
	private String graph;
	
	public ControlFlowGraph() { }
	
	public ControlFlowGraph(Project projectInfo) {
		this.setProjectId(projectInfo);
	}
	
	public ControlFlowGraph(Project projectInfo, String className, String methodName) {
		this.setProjectId(projectInfo);
		this.cgId = ControlFlowGraph.createNew(className, methodName);
	}
	
	public static ControlFlowGraphIdentity createNew(String className, String methodName) {
		
		return new ControlFlowGraphIdentity(className, methodName);
	}
}
