package com.zrpc.remoting.annotation.scan;

import com.zrpc.remoting.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @Author: Zjw
 * @Description: custom scanner registrar
 * @Create 2022-04-17 14:05
 * @Modifier:
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private static final String SPRING_BEAN_BASE_PACKAGE = "com.zrpc";
    private static final String BASE_SCAN_ATTRIBUTE_NAME = "basePackages";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if(annotationAttributes != null){
            rpcScanBasePackages = annotationAttributes.getStringArray(BASE_SCAN_ATTRIBUTE_NAME);
        }
        if(rpcScanBasePackages.length == 0){
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata)importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        CustomScanner rpcScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner springScanner = new CustomScanner(registry, Component.class);
        if(resourceLoader != null){
            rpcScanner.setResourceLoader(resourceLoader);
            springScanner.setResourceLoader(resourceLoader);
        }
        int serviceCount = rpcScanner.scan(rpcScanBasePackages);
        log.info("scan service count : [{}]...", serviceCount);
        int componentCount = springScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("scan component count : [{}]...", componentCount);
    }
}
