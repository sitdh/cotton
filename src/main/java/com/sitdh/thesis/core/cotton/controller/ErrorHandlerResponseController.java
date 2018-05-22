package com.sitdh.thesis.core.cotton.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sitdh.thesis.core.cotton.exception.NoGraphToAnalyzeException;

@ControllerAdvice
public class ErrorHandlerResponseController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {Exception.class, NoGraphToAnalyzeException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
		
		return this.handleExceptionInternal(
				ex, 
				ex.getMessage(), 
				new HttpHeaders(), 
				HttpStatus.CONFLICT, 
				request);
	}
	
	
}
