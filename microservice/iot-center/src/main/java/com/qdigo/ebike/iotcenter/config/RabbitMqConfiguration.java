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

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by niezhao on 2017/1/10.
 */
@Configuration
public class RabbitMqConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("10.80.100.171:5672");//DB2 = 10.80.100.171 or 118.31.164.32
        connectionFactory.setUsername("niezhao");
        connectionFactory.setPassword("niezhao");
        connectionFactory.setVirtualHost("/qdigo");
        connectionFactory.setPublisherConfirms(true); //必须要设置,才能进行消息的回调
        return connectionFactory;
    }

    //-------------------------生产者-----------------------------------
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
//        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
//        messageConverter.addDelgate("application/json", new Jackson2JsonMessageConverter());
        return new Jackson2JsonMessageConverter();
    }


    //------------------------消费者------------------------------------

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory());
//        factory.setMessageConverter(messageConverter());
//        return factory;
//    }


//    @Bean
//    public Queue testQueue() {
//        return new Queue("mytest");
//    }

//    @Bean
//    public TopicExchange pg() {
//        return new TopicExchange("pg");
//    }

}
