package com.health.check;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootTest
class CheckApplicationTests {

	@Autowired
    Environment env;

	@Test
	void printConfig() {
		System.out.println("Profiles = " + Arrays.toString(env.getActiveProfiles()));
		System.out.println("Datasource = " + env.getProperty("spring.datasource.url"));
	}

	@Test
	void contextLoads() {
	}

}
