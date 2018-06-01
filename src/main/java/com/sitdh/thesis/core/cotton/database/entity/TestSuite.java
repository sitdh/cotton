package com.sitdh.thesis.core.cotton.database.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="test_suite")
public class TestSuite {

	@Getter @Setter
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="testsuite_id") @JsonProperty(value="testsuite_id")
	private Integer testsuiteId;
	
	@Getter @Setter @JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="projectId")
	private Project project;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date")
	private Date createddate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastupdate;
	
	@Getter @Setter
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="testSuite")
	private List<TestCase> testcases;
}
