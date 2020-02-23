/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.iotcenter;

import com.qdigo.ebike.iotcenter.dto.other.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@ActiveProfiles({"local"})
class IotCenterApplicationTests {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void test() {
        Connection connection = Connection.builder().timestamp(123).imei("321").connected(true).build();
        rabbitTemplate.convertAndSend("test", connection);
        log.info("发送成功");
    }

}
