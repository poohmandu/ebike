package com.qdigo.ebike.controlcenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableQdigoFeignClients
public class ActivityCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityCenterApplication.class, args);
    }

}

