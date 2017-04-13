/**
 *    Copyright 2010-2016 the original author or authors.
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
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

public class ClassPathServiceScanner extends ClassPathBeanDefinitionScanner {

  private Class<? extends Annotation> annotationClass;

  private Class<?> markerInterface;

  private RpcHandle rpcHandle;

  private String rpcHandleBeanName;

  private ServiceFactoryBean<?> serviceFactoryBean = new ServiceFactoryBean<Object>();

  public ClassPathServiceScanner(BeanDefinitionRegistry registry) {
    super(registry, false);
  }

  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }

  public void setMarkerInterface(Class<?> markerInterface) {
    this.markerInterface = markerInterface;
  }

  public void setServiceFactoryBean(ServiceFactoryBean<?> serviceFactoryBean) {
    this.serviceFactoryBean = serviceFactoryBean != null ? serviceFactoryBean : new ServiceFactoryBean<Object>();
  }

  public void registerFilters() {
    boolean acceptAllInterfaces = true;

    // if specified, use the given annotation and / or marker interface
    if (this.annotationClass != null) {
      addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
      acceptAllInterfaces = false;
    }

    // override AssignableTypeFilter to ignore matches on the actual marker interface
    if (this.markerInterface != null) {
      addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
        @Override
        protected boolean matchClassName(String className) {
          return false;
        }
      });
      acceptAllInterfaces = false;
    }

    if (acceptAllInterfaces) {
      // default include filter that accepts all classes
      addIncludeFilter(new TypeFilter() {
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
          return true;

        }
      });
    }

    // exclude package-info.java
    addExcludeFilter(new TypeFilter() {
      public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        return className.endsWith("package-info");
      }
    });
  }


  @Override
  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

    if (beanDefinitions.isEmpty()) {
      logger.warn("No service interface was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
    } else {
      processBeanDefinitions(beanDefinitions);
    }
    return beanDefinitions;
  }

  public  void removeOtherBeanDefinitionByName(BeanDefinitionHolder holder) {
    try {
      String[] beanNames = ((DefaultListableBeanFactory) this.getRegistry()).getBeanNamesForType(Class.forName(holder.getBeanDefinition().getBeanClassName()));
      for (String beanName:beanNames) {
        if(!holder.getBeanName().equals(beanName) && this.getRegistry().containsBeanDefinition(beanName))
          this.getRegistry().removeBeanDefinition(beanName);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
    System.out.println("***************processBeanDefinitions");
    AbstractBeanDefinition definition;
    for (BeanDefinitionHolder holder : beanDefinitions) {

      definition = (AbstractBeanDefinition) holder.getBeanDefinition();

      removeOtherBeanDefinitionByName(holder);
      if (logger.isDebugEnabled()) {
        logger.debug("Creating ServiceFactoryBean with name '" + holder.getBeanName()
          + "' and '" + definition.getBeanClassName() + "' serviceInterface");
      }
      // the service interface is the original class of the bean
      // but, the actual class of the bean is ServiceFactoryBean
      boolean explicitFactoryUsed = false;
      definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
      definition.setBeanClass(this.serviceFactoryBean.getClass());
      if (StringUtils.hasText(this.rpcHandleBeanName)) {
        definition.getPropertyValues().add("rpcHandle", new RuntimeBeanReference(this.rpcHandleBeanName));
        explicitFactoryUsed = true;
      } else if (this.rpcHandle != null) {
        definition.getPropertyValues().add("rpcHandle", this.rpcHandle);
        explicitFactoryUsed = true;
      }
      if(!explicitFactoryUsed){
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
      }
    }


  }


  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
  }


  @Override
  protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
    if (super.checkCandidate(beanName, beanDefinition)) {
      return true;
    } else {
      logger.warn("Skipping ServiceFactoryBean with name '" + beanName
          + "' and '" + beanDefinition.getBeanClassName() + "' ServiceInterface"
          + ". Bean already defined with the same name!");
      return false;
    }
  }

  public Class<? extends Annotation> getAnnotationClass() {
    return annotationClass;
  }

  public Class<?> getMarkerInterface() {
    return markerInterface;
  }

  public RpcHandle getRpcHandle() {
    return rpcHandle;
  }

  public void setRpcHandle(RpcHandle rpcHandle) {
    this.rpcHandle = rpcHandle;
  }

  public String getRpcHandleBeanName() {
    return rpcHandleBeanName;
  }

  public void setRpcHandleBeanName(String rpcHandleBeanName) {
    this.rpcHandleBeanName = rpcHandleBeanName;
  }
}
