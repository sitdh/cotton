package com.sitdh.thesis.core.cotton.database.entity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitdh.thesis.core.cotton.service.entity.PackageDateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="test_case")
public class TestCase {
	
	@Getter @Setter
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="testcase_id") @JsonProperty(value="testcase_id")
	private Integer testcaseId;
	
	@Getter @Setter
	@Column(name="testcase_name") @JsonProperty(value="name")
	private String testcaseName;
	
	@Getter @Setter
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="testsuiteId") @JsonIgnore
	private TestSuite testSuite;
	
	@Getter @Setter
	@Column(name="test_location") @JsonProperty(value="location")
	private String testLocation;
	
	@Temporal(TemporalType.TIMESTAMP) 
	@Column(name="created_date") @JsonProperty("created_date")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=PackageDateTimeFormat.FULL, timezone="Asia/Bangkok")
	private Date createddate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update") @JsonProperty("created_date")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=PackageDateTimeFormat.FULL, timezone="Asia/Bangkok")
	private Date lastupdate;
}
