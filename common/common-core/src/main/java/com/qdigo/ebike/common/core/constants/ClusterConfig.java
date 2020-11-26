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

package com.qdigo.ebike.common.core.constants;

import com.qdigo.ebike.common.core.util.http.NetUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by niezhao on 2018/3/29.
 */
@Slf4j
//rebuild 应该删除
public final class ClusterConfig {

    private final static Config[] CONFIGS = {
        Config.builder().name("biz1").wanIp("118.178.224.165").lanIp("10.27.213.11").MQConsumers(2).MQPrefetch(1).build(),
        Config.builder().name("biz2").wanIp("118.31.185.15").lanIp("10.80.63.30").MQConsumers(2).MQPrefetch(1).build(),
        Config.builder().name("biz3").wanIp("116.62.52.201").lanIp("10.28.254.226").MQConsumers(2).MQPrefetch(1).build(),
        Config.builder().name("test1").wanIp("118.31.103.21").lanIp("10.51.235.226").MQConsumers(2).MQPrefetch(1).build(),
        Config.builder().name("db1").wanIp("101.37.84.147").lanIp("10.28.147.20").MQConsumers(2).MQPrefetch(1).build(),
        Config.builder().name("db2").wanIp("118.31.164.32").lanIp("10.80.100.171").MQConsumers(4).MQPrefetch(10).build()
    };

    private static Config defaultConfig() {
        return Config.builder().name("").wanIp(NetUtil.getIp()).lanIp(NetUtil.getIp())
            .MQConsumers(2).MQPrefetch(1).build();
    }

    public final static Config config = getConfig(NetUtil.getIp());

    public static Config getConfig(String wanIp) {
        for (Config cfg : CONFIGS) {
            if (cfg.wanIp.equals(wanIp)) {
                return cfg;
            }
        }
        return defaultConfig();
    }

    @Getter
    @Builder
    public static class Config {
        private String name;
        private String wanIp;
        private String lanIp;
        private int MQConsumers;
        private int MQPrefetch;
    }

}
