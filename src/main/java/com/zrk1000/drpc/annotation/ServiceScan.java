package com.zrk1000.drpc.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by pc on 2017/3/13.
 * 扫描Service接口
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ServiceScannerRegistrar.class)
public @interface ServiceScan {

    String value() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    Class<? extends Annotation> annotationClass() default Annotation.class;

    Class<?> markerInterface() default Class.class;

    String rpcHandleBeanRef() default "stormLocalDrpcHandle";


}
