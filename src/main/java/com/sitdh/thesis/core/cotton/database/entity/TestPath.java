package com.sitdh.thesis.core.cotton.database.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="test_path")
public class TestPath {

	@Getter @Setter
	@Id @Column(name="path_id", length=50)
	private String pathId;
	
	@Getter @Setter @Lob
	@Column(name="steps")
	private String steps;
	
	@Getter @Setter
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="projectId")
	private Project project;
}
