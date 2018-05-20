package com.sitdh.thesis.core.cotton.exception;

import lombok.Getter;
import lombok.Setter;

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
