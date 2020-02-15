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

package com.qdigo.ebike.iotcenter.config;

import org.springframework.stereotype.Component;

/**
 * Created by niezhao on 2017/1/11.
 */
@Component
public class Listener {

//    @RabbitListener(queues = "mytest")
//    public void process(String hello) {
//        System.out.println("Receiver : " + hello);
//    }

//    @RabbitListener(bindings = @QueueBinding(
//            exchange = @Exchange(value = "pg", type = ExchangeTypes.TOPIC),
//            value = @Queue(value = "up.pg.test", autoDelete = "true", durable = "true"),
//            ignoreDeclarationExceptions = "true",
//            key = "up.pg"))
//    private void Receiver(PGReqDto pg) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        String string = mapper.writeValueAsString(pg);
//        System.out.println(string);
//    }
}
