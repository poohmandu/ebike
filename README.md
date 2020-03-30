# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)

--------------

#上传私有jar包到制品库

```bash
mvn deploy:deploy-file -Dfile=alipay-sdk-java-1.0.jar \
-DgroupId=com.alibaba -DartifactId=alipay-sdk-java -Dversion=1.0 -Dpackaging=jar \
-DrepositoryId=qdigo-diandichuxing-qdigo_maven \
-Durl=https://qdigo-maven.pkg.coding.net/repository/diandichuxing/qdigo_maven/
```


相关问题
==============

##### mybatis:
+ mybatis 3.5.4 版本后useActualParamName为true,出现 `Parameter '0' not found`
    
##### spring boot:

   
##### docker: 
+ 较低版本jdk 无法感知容器,升级jdk后使用参数`-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap`
