/**
 *    Copyright 2010-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.zrk1000.drpc.spring;

import com.zrk1000.drpc.rpc.RpcHandle;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.springframework.util.Assert.notNull;


public class ServiceScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

  private String basePackage ;

  private RpcHandle rpcHandle;

  private Class<? extends Annotation> annotationClass;

  private Class<?> markerInterface;

  private ApplicationContext applicationContext;

//  private WebApplicationContext subWac;

  private String beanName;

  private boolean processPropertyPlaceHolders;

  private BeanNameGenerator nameGenerator;

  public void setBasePackage(String basePackage) {
    this.basePackage = basePackage;
  }

  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }
  public void setMarkerInterface(Class<?> superClass) {
    this.markerInterface = superClass;
  }
  public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
    this.processPropertyPlaceHolders = processPropertyPlaceHolders;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
//    //获取父容器
//    WebApplicationContext rootWac= ContextLoader.getCurrentWebApplicationContext();
//    //获取servletContext
//    ServletContext servletContext = rootWac.getServletContext();
//    //获取子容器
//    WebApplicationContext subWac= WebApplicationContextUtils.getWebApplicationContext(servletContext,
//            "org.springframework.web.servlet.FrameworkServlet.CONTEXT.springServlet" );
//    this.subWac = subWac;
  }

  public void setBeanName(String name) {
    this.beanName = name;
  }

  public BeanNameGenerator getNameGenerator() {
    return nameGenerator;
  }

  public void setNameGenerator(BeanNameGenerator nameGenerator) {
    this.nameGenerator = nameGenerator;
  }


  public void afterPropertiesSet() throws Exception {
    notNull(this.basePackage, "Property 'basePackage' is required");
  }

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // left intentionally blank
    System.out.println("postProcessBeanFactory");
  }

  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    System.out.println("postProcessBeanDefinitionRegistry");
    if (this.processPropertyPlaceHolders) {
      processPropertyPlaceHolders();
    }

    ClassPathServiceScanner scanner = new ClassPathServiceScanner(registry);
    scanner.setAnnotationClass(this.annotationClass);
    scanner.setMarkerInterface(this.markerInterface);
    scanner.setResourceLoader(this.applicationContext);
    scanner.setBeanNameGenerator(this.nameGenerator);
    scanner.setRpcHandle(this.rpcHandle);
    scanner.registerFilters();
    scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
  }

  public void processPropertyPlaceHolders() {
    Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);

    if (!prcs.isEmpty() && applicationContext instanceof GenericApplicationContext) {
      BeanDefinition mapperScannerBean = ((GenericApplicationContext) applicationContext)
          .getBeanFactory().getBeanDefinition(beanName);

      DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
      factory.registerBeanDefinition(beanName, mapperScannerBean);

      for (PropertyResourceConfigurer prc : prcs.values()) {
        prc.postProcessBeanFactory(factory);
      }

      PropertyValues values = mapperScannerBean.getPropertyValues();

      this.basePackage = updatePropertyValue("basePackage", values);
    }
  }

  public String updatePropertyValue(String propertyName, PropertyValues values) {
    PropertyValue property = values.getPropertyValue(propertyName);

    if (property == null) {
      return null;
    }

    Object value = property.getValue();

    if (value == null) {
      return null;
    } else if (value instanceof String) {
      return value.toString();
    } else if (value instanceof TypedStringValue) {
      return ((TypedStringValue) value).getValue();
    } else {
      return null;
    }
  }

  public RpcHandle getRpcHandle() {
    return rpcHandle;
  }

  public void setRpcHandle(RpcHandle rpcHandle) {
    this.rpcHandle = rpcHandle;
  }

  public String getBasePackage() {
    return basePackage;
  }

  public Class<? extends Annotation> getAnnotationClass() {
    return annotationClass;
  }

  public Class<?> getMarkerInterface() {
    return markerInterface;
  }

  public String getBeanName() {
    return beanName;
  }

  public boolean isProcessPropertyPlaceHolders() {
    return processPropertyPlaceHolders;
  }
}
