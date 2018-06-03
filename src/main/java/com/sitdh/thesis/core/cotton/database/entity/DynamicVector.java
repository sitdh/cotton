package com.sitdh.thesis.core.cotton.database.entity;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name="dynamic_logger")
public class DynamicVector {

	@Getter @Setter @JsonIgnore
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="log_id")
	private Integer logId;
	
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
	
	public DynamicVector(GraphVector gv) {
		this.setSource(gv.getSource());
		this.setEdge(gv.getEdge());
		this.setTarget(gv.getTarget());
	}
	
	public boolean equals(Vector o) {
    	
    	return this.getSource().equals(o.getSource()) 
    			&& this.getTarget().equals(o.getTarget()) 
    			&& this.getEdge().equals(o.getEdge());
    }
    
    public int hashCode() {
    	return Objects.hash(this.getSource() + this.getEdge() + this.getSource());
    }
}
