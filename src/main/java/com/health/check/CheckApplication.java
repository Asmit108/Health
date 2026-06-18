package com.health.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Health Check Application.
 * This is a Spring Boot application that provides a comprehensive health check
 * system with appointments, symptoms analysis, and patient-doctor interactions.
 *
 * @author Health Check Team
 * @version 1.0
 */
@SpringBootApplication
public class CheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckApplication.class, args);
	}

}
