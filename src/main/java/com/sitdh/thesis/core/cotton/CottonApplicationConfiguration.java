package com.sitdh.thesis.core.cotton;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class CottonApplicationConfiguration {
	
	@Bean
	public HttpHeaders getDefaultHeaders() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.setAccessControlAllowOrigin("*");
		
		List<HttpMethod> allowMethod = new ArrayList<HttpMethod>();
		allowMethod.add(HttpMethod.GET);
		allowMethod.add(HttpMethod.POST);
		
		headers.setAccessControlAllowMethods(allowMethod);
		
		return headers;
	}

}
