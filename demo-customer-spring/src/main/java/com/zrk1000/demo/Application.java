package com.zrk1000.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


//@MapperScan("com.zrk1000.proxytest.mapper")
//@ImportResource("spring/spring-context.xml")
//@ComponentScan("")
@RestController
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	protected static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		String[] profiles = ctx.getEnvironment().getActiveProfiles();
		logger.info("spring BOOT profiles:" + Arrays.toString(profiles));
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}
