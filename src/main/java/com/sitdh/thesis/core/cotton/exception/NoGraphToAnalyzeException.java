package com.sitdh.thesis.core.cotton.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoGraphToAnalyzeException extends Exception {
	
	@Getter
	public String message;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8972066448569477735L;

	public NoGraphToAnalyzeException() {
		 this.message = "Graph information not enough or no connected class within interested package";
	}
}
