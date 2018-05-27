package com.sitdh.thesis.core.cotton.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="control_flow_graph")
public class ControlFlowGraph {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cg_id") @Getter @Setter
	private Integer cgId;
	
	@Getter @Setter
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="projectId")
	private Project projectId;
	
	@Getter @Setter
	@Column(name="class_name")
	private String className;
	
	@Getter @Setter
	@Column(name="method_name")
	private String methodName;

	@Getter @Setter @Lob
	@Column(name="graph")
	private String graph;
	
	public ControlFlowGraph() { }
	
	public ControlFlowGraph(Project projectInfo) {
		this.setProjectId(projectInfo);
	}
}
