FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER niezhao nz@qdigo.com

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /app

WORKDIR /app

ARG PORT

ARG JAR_FILE

EXPOSE ${PORT}

ADD ${JAR_FILE} app.jar

