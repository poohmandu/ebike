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

package com.qdigo.ebike.iotcenter.util;


import com.qdigo.ebike.iotcenter.config.ConfigConst;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.function.Consumer;


public class RedisUtil {
    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private static JedisPool jedisPool = null;// 非切片连接池
    private static final Object lockObj = new Object();
    private static final String REDIS_SERVER_HOST;
    private static final int REDIS_SERVER_PORT = 63791;
    private static final int REDIS_SERVER_MAXWAIT = 5000;
    private static final int REDIS_SERVER_MAXTOTAL = 500;
    private static final int REDIS_SERVER_MAXIDLE = 50;
    private static final int REDIS_SERVER_TIMEOUT = 5000;
    private static final boolean REDIS_SERVER_TESTONBORROW = true;
    private static final int REDIS_SERVER_DEFAULTDB = 0;
    private static final String REDIS_SERVER_PASSWORD = "ad069ce88af803360adf083ba347500c";

    static {
        if (ConfigConst.env.equals("test")) {//测试环境
            REDIS_SERVER_HOST = "192.168.2.185";
        } else if (ConfigConst.env.equals("dev")) {
            REDIS_SERVER_HOST = "101.37.84.147"; //DB1外网
        } else {
            REDIS_SERVER_HOST = "100.115.147.146"; //prod
        }
    }

    /****
     * 通过配置得到 Jedis
     *
     * @return Jedis实例
     */
    public static Jedis getConnection() {
        Jedis retJedis = null;
        try {
            if (jedisPool == null) {
                synchronized (lockObj) {
                    if (jedisPool != null) {
                        jedisPool.destroy();
                    }
                    JedisPoolConfig config = new JedisPoolConfig();
                    config.setMaxTotal(REDIS_SERVER_MAXTOTAL);
                    config.setMaxIdle(REDIS_SERVER_MAXIDLE);
                    config.setMaxWaitMillis(REDIS_SERVER_MAXWAIT);
                    config.setTestOnBorrow(REDIS_SERVER_TESTONBORROW);

                    // 如再发生 redis 连接获取不到 尝试以下
                    // http://www.cnblogs.com/qlong8807/p/5149007.html
                    //config.setTestWhileIdle(true);
                    //config.setTimeBetweenEvictionRunsMillis(30000);
                    //config.setNumTestsPerEvictionRun(10);
                    //config.setMinEvictableIdleTimeMillis(90000);

                    String password = REDIS_SERVER_PASSWORD;
                    if (StringUtils.isBlank(password)) {// 有设置密码
                        jedisPool = new JedisPool(config, REDIS_SERVER_HOST, REDIS_SERVER_PORT, REDIS_SERVER_TIMEOUT);
                    } else {
                        jedisPool = new JedisPool(config, REDIS_SERVER_HOST, REDIS_SERVER_PORT, REDIS_SERVER_TIMEOUT, password);
                    }
                }
            }
            retJedis = jedisPool.getResource();
            retJedis.select(REDIS_SERVER_DEFAULTDB);
        } catch (Exception e) {
            logger.error("获取redis连接池失败", e);
            throw new RuntimeException(e);
        }
        return retJedis;
    }

    /***
     * 释放资源
     *
     * @param jedis
     */
    private void returnResource(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("jedis释放连接失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * hash 存储
     *
     * @param key
     * @param field
     * @param value
     */
    public final void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("set设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }
    }

    public final void expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("expire设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }
    }

    public final void opsForJedis(Consumer<Jedis> action) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            action.accept(jedis);
        } catch (Exception e) {
            logger.error("expire设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }
    }


    public final void listPush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("listPush设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }
    }


    public final void set(String key, String value, Integer expire) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.set(key, value);
            if (null != expire) {
                jedis.expire(key, expire);
            }
        } catch (Exception e) {
            logger.error("set设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }

    }

    public final void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("set设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 存储map数据接口
     *
     * @param key
     * @param mapValue
     */
    public final void hmSet(String key, Map<String, String> mapValue) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            jedis.hmset(key, mapValue);
        } catch (Exception e) {
            logger.error("set设值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }

    }

    public final String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getConnection();
            String value = jedis.get(key);
            return value;
        } catch (Exception e) {
            logger.error("get取值失败", e);
            throw new RuntimeException(e);
        } finally {
            returnResource(jedis);
        }
    }

}
