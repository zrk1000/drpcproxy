package com.zrk1000.demo;

import com.zrk1000.demo.repository.GroupRepository;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/3
 * Time: 14:00
 */
public class ApplicationConfigTest {
    @Test
    public void bootstrapAppFromJavaConfig() {

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        assertThat(context, is(notNullValue()));
        assertThat(context.getBean(GroupRepository.class), is(notNullValue()));
    }

    @Test
    public void bootstrapAppFromBasePackages() {

        ApplicationContext context = new AnnotationConfigApplicationContext("com.zrk1000.demo");
        assertThat(context, is(notNullValue()));
        assertThat(context.getBean(GroupRepository.class), is(notNullValue()));
    }

}

    