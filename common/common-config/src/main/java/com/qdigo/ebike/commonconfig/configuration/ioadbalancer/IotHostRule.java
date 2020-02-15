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

package com.qdigo.ebike.commonconfig.configuration.ioadbalancer;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * Description: 
 * date: 2020/2/15 6:11 PM
 * @author niezhao
 * @version
 * @since JDK 1.8
 */
@Slf4j
public class IotHostRule extends AbstractLoadBalancerRule {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @NacosInjected
    private NamingService namingService;

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        //读取配置文件，并初始化IotHostRule
    }

    @Override
    public Server choose(Object key) {
        try {


            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // log.info("lb = {}", loadBalancer);

            // 想要请求的微服务的名称
            String name = loadBalancer.getName();

            // 拿到服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // nacos client自动通过基于权重的负载均衡算法，给我们选择一个实例。
            Instance instance = namingService.selectOneHealthyInstance(name);

            log.info("选择的实例是：port = {}, instance = {}", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}
