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
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import com.qdigo.ebike.common.core.constants.ConfigConstants;
import com.qdigo.ebike.common.core.constants.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${qdigo.netty.iot-service-name}")
    private String iotServiceName;

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        //读取配置文件，并初始化IotHostRule
    }

    @Override
    @SneakyThrows
    public Server choose(Object key) {
        String imei = "";
        String k = Keys.available_slave.getKey(imei.substring(ConfigConstants.imei.getConstant().length()));
        Instance instance = namingService.getAllInstances(iotServiceName).stream()
                .filter(Instance::isHealthy)
                .filter(ins -> {
                    String s = redisTemplate.opsForValue().get(k);
                    return s != null && s.equals(ins.getIp());
                }).findAny().orElse(null);
        if (instance != null) {
            return new NacosServer(instance);
        }
        return null;
    }
}
