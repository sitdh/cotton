package com.sitdh.thesis.core.cotton.database.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

public class FileName {

	@Getter @Setter
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="file_id", columnDefinition="BIGINT")
	private Integer fileId;
	
	@Getter @Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="containerId")
	private Project container;
	
	@Getter @Setter
	private String filename;
	
	@Getter @Setter
	private String location;
	
	@Getter @Setter
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="fileName")
	private List<ConstantCollection> constants;
	
	public FileName() { }
	
	public FileName(Project container, String filename, String location) {
		this.setContainer(container);
		this.setFilename(filename);
		this.setLocation(location);
	}
}
