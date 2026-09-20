package com.learningplatform.coursecreation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CourseCreationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseCreationServiceApplication.class, args);
	}

}
