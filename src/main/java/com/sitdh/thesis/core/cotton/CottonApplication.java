package com.sitdh.thesis.core.cotton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EntityScan
@EnableWebMvc
@SpringBootApplication
@Import({CottonApplicationConfiguration.class})
public class CottonApplication {

	public static void main(String[] args) {
		SpringApplication.run(CottonApplication.class, args);
	}
}
