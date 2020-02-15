package com.qdigo.ebike.usercenter;

import com.qdigo.ebike.api.EnableQdigoFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author niezhao
 */
@EnableQdigoFeignClients
@SpringCloudApplication
public class UserCenterApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

}
