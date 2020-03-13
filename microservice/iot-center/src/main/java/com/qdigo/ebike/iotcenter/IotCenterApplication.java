package com.qdigo.ebike.iotcenter;

import com.qdigo.ebike.iotcenter.netty.SocketServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.Resource;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class IotCenterApplication implements CommandLineRunner {

    @Resource
    private SocketServer socketServer;

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IotCenterApplication.class);
        //app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @SneakyThrows
    @Override
    public void run(String... args) {
        //socketServer.start();
        new Thread(() -> {
            try {
                socketServer.start();
            } catch (Exception e) {
                log.error("netty服务发生异常:", e);
            }
        }, "qdigo-netty-server").start();
    }
}
