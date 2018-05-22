package com.sitdh.thesis.core.cotton.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PackageNotInterestedException extends Exception {
	
	@Getter @Setter
	private String message;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6282235194335493139L;

	public PackageNotInterestedException() { 
		this.message = "Class doesn't container inside interested package";
	}
}
