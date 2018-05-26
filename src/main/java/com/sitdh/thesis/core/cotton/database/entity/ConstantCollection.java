package com.sitdh.thesis.core.cotton.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sitdh.thesis.core.cotton.database.entity.constants.ConstantType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="constant_collection")
public class ConstantCollection {

	@Getter @Setter
	@Id @GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="constant_id")
	private Integer constantId;
	
	@Getter @Setter @Column(name="project_id")
	private String projectId;
	
	@Getter @Setter
	private String type;
	
	@Getter @Setter
	private String value;
	
	public ConstantCollection() { }
	
	public ConstantCollection(String projectId, String type, String value) {
		this.setProjectId(projectId);
		this.setType(type);
		this.setValue(value);
	}
}
