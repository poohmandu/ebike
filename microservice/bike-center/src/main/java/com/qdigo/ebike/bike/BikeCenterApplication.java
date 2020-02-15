package com.qdigo.ebike.bike;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableQdigoFeignClients
@SpringCloudApplication
@MapperScan("com.qdigo.ebike.bike.mapper")
public class BikeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(BikeCenterApplication.class, args);
    }

}
