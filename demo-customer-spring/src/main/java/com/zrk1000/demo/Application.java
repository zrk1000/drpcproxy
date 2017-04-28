package com.zrk1000.demo;


import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.TestService;
import com.zrk1000.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


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

	@Autowired
	private UserService userService;

	@Autowired
	private TestService testService;



	@RequestMapping("/user")
	public User user(@RequestParam(required = false) String name){

		User result = userService.getUser(name);
		return result;
	}

	@RequestMapping("/test")
	public Map<String, String> test(){
		testService.basedTypeParameter(1111111,22222L,3333D,true,(byte)55,'6',7.0F,(short)8);
        testService.retunrVoid();
        ArrayList<String> list = new ArrayList<String>();
        list.add("11111111");
        list.add("2222222222");
        Map<String, String> map = testService.complexTypes(list);
        return map;
	}
}
