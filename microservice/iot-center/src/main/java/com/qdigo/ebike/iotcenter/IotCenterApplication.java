package com.qdigo.ebike.iotcenter;

import com.qdigo.ebike.iotcenter.netty.SocketServer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotCenterApplication implements CommandLineRunner {

    @Autowired
    private SocketServer socketServer;

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication app  = new SpringApplication(IotCenterApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        socketServer.start();
    }
}
