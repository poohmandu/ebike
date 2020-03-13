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

package com.qdigo.ebike.commonconfig.configuration;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 
 * date: 2020/2/22 11:38 AM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
public class RabbitmqConfiguration {

    //生产者
    //@Bean
    //public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    //    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    //    template.setMessageConverter(new Jackson2JsonMessageConverter());
    //    return template;
    //}

    /**
     *  由RabbitAutoConfiguration源码可知存在MessageConverter的bean会自动加载
     *  ObjectProvider 提供可能的注入
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //消费者
    //@Bean("rabbitListenerContainerFactory")
    //@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type",
    //        havingValue = "simple", matchIfMissing = true)
    //SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
    //        SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
    //    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    //    configurer.configure(factory, connectionFactory);
    //    factory.setMessageConverter(messageConverter());
    //    return factory;
    //}

}
