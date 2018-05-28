package com.sitdh.thesis.core.cotton.database.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Embeddable
public class ControlFlowGraphIdentity implements Serializable {
	
	@JsonIgnore
	private static final long serialVersionUID = -4420250385358961741L;

	@Getter @Setter
	@Column(name="class_name")
	private String className;
	
	@Getter @Setter
	@Column(name="method_name")
	private String methodName;
	
	public ControlFlowGraphIdentity() { }
	
	public ControlFlowGraphIdentity(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}
}
