package com.sitdh.thesis.core.cotton.database.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitdh.thesis.core.cotton.analyzer.data.GraphVector;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class Vector {

	@Getter @Setter
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer vid;
	
	@Getter @Setter
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="projectId") @JsonIgnore
	private Project project;
	
	@Getter @Setter
	private String source;
	
	@Getter @Setter
	private String target;
	
	@Getter @Setter
	private String edge;
	
	public Vector() { }
	
	public Vector(GraphVector gv) {
		this.setSource(gv.getSource());
		this.setEdge(gv.getEdge());
		this.setTarget(gv.getTarget());
	}
	
	
}
