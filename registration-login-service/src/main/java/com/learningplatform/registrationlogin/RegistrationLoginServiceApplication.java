package com.learningplatform.registrationlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RegistrationLoginServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistrationLoginServiceApplication.class, args);
    }
}