FROM openjdk:8u181-jdk-alpine

MAINTAINER niezhao nz@qdigo.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ENV JAVA_OPTS="-Xmx512m -Xms256m -Xss256k -server -XX:+TieredCompilation \
-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

ADD wait /script