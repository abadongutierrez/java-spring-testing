package com.jabaddon.learning.java_spring_testing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class JavaSpringTestingApplicationTests {

	@Test
	void contextLoads() {
	}

}
