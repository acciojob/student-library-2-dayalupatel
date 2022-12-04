package com.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class  StudentLibraryApplication{

	public static void main(String[] args) {
		SpringApplication.run(StudentLibraryApplication.class, args);
	}

}
