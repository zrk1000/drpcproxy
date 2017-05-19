package com.zrk1000.demo;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/3
 * Time: 14:10
 */
//@Configuration
//@ComponentScan(basePackages={"com.zrk1000.demo"})
//@SpringBootConfiguration
//@EnableAutoConfiguration
//@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class))
//@EnableJpaRepositories
//@Import({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@SpringBootApplication
public class ApplicationConfig {
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            EntityManagerFactoryBuilder factory, DataSource dataSource,
//            JpaProperties properties ,UserInterceptor userInterceptor) {
//        Map<String, Object> jpaProperties = new HashMap<String, Object>();
//        jpaProperties.putAll(properties.getHibernateProperties(dataSource));
//        jpaProperties.put("hibernate.ejb.interceptor", "com.zrk1000.demo.UserInterceptor");
//        return factory.dataSource(dataSource).packages("com.zrk1000.demo")
//                .properties((Map) jpaProperties).build();
//    }

}

    