package com.jabaddon.learning.java_spring_testing;

import org.springframework.boot.SpringApplication;

public class TestJavaSpringTestingApplication {

	public static void main(String[] args) {
		SpringApplication.from(JavaSpringTestingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
