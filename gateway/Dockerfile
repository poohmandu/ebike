FROM openjdk:8u181-jdk-alpine

MAINTAINER niezhao nz@qdigo.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /app

WORKDIR /app

ARG PORT
ARG JAR_FILE
ENV JAVA_OPTS="-Xmx256m -Xms256m -Xss256k -server -XX:+TieredCompilation \
-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

#java8u131 以后支持感知容器
ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar

EXPOSE ${PORT}

ADD ${JAR_FILE} app.jar