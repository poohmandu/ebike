package com.qdigo.ebike.agentcenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQdigoFeignClients
@SpringBootApplication
public class AgentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentCenterApplication.class, args);
    }

}
