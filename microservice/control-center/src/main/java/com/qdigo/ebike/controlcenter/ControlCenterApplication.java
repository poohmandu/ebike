package com.qdigo.ebike.controlcenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQdigoFeignClients
@SpringBootApplication
public class ControlCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControlCenterApplication.class, args);
    }

}
