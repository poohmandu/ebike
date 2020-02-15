package com.qdigo.ebike.iotcenter;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotCenterApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication app  = new SpringApplication(IotCenterApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        SocketServer.run();
    }

}
