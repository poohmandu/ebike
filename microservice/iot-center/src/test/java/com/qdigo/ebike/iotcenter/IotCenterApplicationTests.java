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
import org.apache.commons.lang3.StringUtils;
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

    @Test
    void testHost() {
        String str1 = "server.natappfree.cc";
        String str2 = "32860";
        String str3 = "1";
        char[] chars1 = str1.toCharArray();
        int val1 = 0;
        for (char aChar : chars1) {
            val1 += (int) aChar;
        }

        char[] chars2 = str2.toCharArray();
        int val2 = 0;
        for (char aChar : chars2) {
            val2 += (int) aChar;
        }

        char[] chars3 = str3.toCharArray();
        int val3 = 0;
        for (char aChar : chars3) {
            val3 += (int) aChar;
        }
        int val = val1 + val2 + val3;
        String hexString = Integer.toHexString(val);
        String substring = StringUtils.substring(hexString, -2);
        int result = Integer.parseInt(substring, 16);
        System.out.println(result);
    }

}
