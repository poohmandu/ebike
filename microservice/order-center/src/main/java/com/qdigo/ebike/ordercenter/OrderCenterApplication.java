package com.qdigo.ebike.ordercenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQdigoFeignClients
@SpringBootApplication
public class OrderCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCenterApplication.class, args);
    }

}
