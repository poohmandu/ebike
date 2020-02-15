package com.qdigo.ebike.stationcenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQdigoFeignClients
@SpringBootApplication
@MapperScan("com.qdigo.ebike.stationcenter.mapper")
public class StationCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StationCenterApplication.class, args);
    }

}
