package com.psycology.backend;

import com.psycology.backend.config.BrevoProperties;
import com.psycology.backend.config.GroqProperties;
import com.psycology.backend.config.RazorpayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BrevoProperties.class, GroqProperties.class, RazorpayProperties.class})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
