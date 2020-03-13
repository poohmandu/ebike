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

package com.qdigo.ebike.controlcenter.service.inner.cammand;


import com.qdigo.ebike.common.core.constants.*;
import com.qdigo.ebike.common.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by niezhao on 2017/3/30.
 */
//rebuild 不用同步等待
public class Connection {

    //redis:   pre:imei:cmd:  valueList:timestamp:

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    //private static final String MASTER_DOMAIN = "mpktsvr1.qdigo.com";
    //private static final String BACKUP_DOMAIN = "spktsvr1.qdigo.com";

    private static int PORT = 13078;
    private static int TIME_OUT = Const.deviceTimeout;

    private Socket socket;
    private String ip; //此连接最新的ip
    private String imei; //此连接属于谁

    private static final ConcurrentHashMap<String, Connection> maps = new ConcurrentHashMap<>();

    private Connection() {
    }

    private Connection(String currentIp) {
        try {
            this.socket = new Socket(currentIp, PORT);
            this.socket.setSoTimeout(TIME_OUT);
            this.ip = currentIp;
            logger.debug("创建单例socket连接,主机{},端口{}", this.ip, PORT);
        } catch (IOException e) {
            logger.error("创建socket连接失败", e);
        }
    }

    // TODO: 是否需要 synchronized 防重入
    static Connection getInstance(String imei) {
        String key = Keys.getKey(Keys.available_slave, imei.substring(ConfigConstants.imei.getConstant().length()));
        RedisTemplate<String, String> redisTemplate = SpringContextHolder.getBean(RedisTemplate.class);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();

        Connection connection = maps.get(imei);
        String wanIP = hash.get(key, "AVAILABLE_SLAVE");
        String lanIp = getConnIp(wanIP);

        if (connection != null && connection.socket != null && lanIp.equals(connection.ip) && !connection.isServerClose()) {
            logger.debug("{}检测到连接有效,复用该连接", imei);
            return connection;
        } else if (connection == null) {
            logger.debug("{}重新创建:映射表里connection为空", imei);
        } else if (!lanIp.equals(connection.ip)) {
            logger.debug("{}重新创建:客户端最新ip与本连接不同", imei);
        } else if (connection.socket == null) {
            logger.debug("{}重新创建:连接的socket已置为空", imei);
        } else {
            logger.debug("{}重新创建:检测到连接关闭", imei);
        }
        //新建连接
        connection = new Connection(lanIp);
        connection.imei = imei;
        maps.put(imei, connection);
        return connection;
    }

    static Connection getChargerInstance(String imei) {
        String key = Keys.getKey(Keys.available_slave_charger, imei.substring(7));
        RedisTemplate<String, String> redisTemplate =  SpringContextHolder.getBean(RedisTemplate.class);
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();

        Connection connection = maps.get(imei);
        if (connection != null && connection.socket != null && !connection.isServerClose()) {
            logger.debug("{}检测到连接有效,复用该充电桩连接", imei);
            return connection;
        } else if (connection == null) {
            logger.debug("{}的map中没有对应连接,正在重新创建充电桩连接", imei);
        } else {
            logger.debug("{}检测到连接已关闭,准备重新创建充电桩连接", imei);
        }
        String wanIP = hash.get(key, "AVAILABLE_SLAVE");
        String lanIp = getConnIp(wanIP);

        //新建连接
        connection = new Connection(lanIp);
        connection.imei = imei;
        maps.put(imei, connection);
        return connection;
    }

    private static String getConnIp(String currentIP) {
        Environment env = SpringContextHolder.getBean(Environment.class);
        //rebuild
        if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
            String lanIp = ClusterConfig.getConfig(currentIP).getLanIp();
            if (lanIp != null) {
                return lanIp;
            }
        }
        return currentIP;
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @return
     */
    private Boolean isServerClose() {
        try {
            // 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常
            this.socket.sendUrgentData(0xFF);
            logger.debug("监测到连接有效({}-{})", this.imei, this.ip);
            return false;
        } catch (Exception se) {
            logger.debug("监测到连接关闭({}-{})", this.imei, this.ip);
            return true;
        }
    }

    protected void destroy() {
        try {
            logger.info("销毁此socket连接({}-{}):", this.imei, this.ip);
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
            logger.error("销毁socket连接失败:", e);
        }
    }

    private String readInfoStream(InputStream input) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
                if (result.toString().contains("bye")) {
                    break;
                }
            }
            logger.debug("socket返回信息：\n" + result.toString());
            if (result.toString().contains("success")) {
                return "okay";
            }
        } catch (IOException e) {
            logger.error("socket读取信息异常:{}", e.getMessage());
        }
        return "failed";
    }

    public void sendNoResponse(byte[] cmd, Consumer<byte[]> consumer) {
        OutputStream os;
        try {
            os = this.socket.getOutputStream();
            os.write(cmd);
            os.flush();
            logger.debug("成功发送指令:{},不等待响应", Arrays.toString(cmd));
            consumer.accept(cmd);
        } catch (IOException e) {
            logger.error("sendNoResponse发生socket错误:" + e.getMessage());
        }
    }

    //发送一次命令
    private boolean Send(byte[] cmd, Consumer<byte[]> consumer) {
        OutputStream os;
        InputStream is;
        try {
            os = this.socket.getOutputStream();
            os.write(cmd);
            os.flush();
            logger.debug("成功发送指令:{},开始接受响应...", Arrays.toString(cmd));
            consumer.accept(cmd);

            // 发送成功后,会有响应
            is = this.socket.getInputStream();
            String result = this.readInfoStream(is);
            logger.debug("获取到输入流,返回结果为:" + result);
            return result.equals("okay");
            //return true;
        } catch (IOException e) {
            logger.error("Send发生socket错误:" + e.getMessage());
            return false;
        }
    }

    private boolean Send(byte[] cmd) {
        return Send(cmd, bytes -> {
        });
    }

    //发送命令;如果失败,重新连接 (2次)
    boolean SendCmd(byte[] cmd, Consumer<byte[]> consumer, int count) {
        try {
            boolean bln;
            for (int i = 0; i < count; i++) {
                logger.debug("socket第{}次发送命令", i + 1);
                bln = Send(cmd, consumer);
                if (bln) {
                    return true;
                }
                if (count > 1) {
                    TimeUnit.MILLISECONDS.sleep(1500);
                }
            }
            logger.warn("socket的" + count + "次发送命令都失败");
            //this.destroy();
            //this.socket = null;
        } catch (Throwable e) {
            logger.error("iotSdk发生异常:{}", e.getMessage());
        } finally {
            this.destroy();
            this.socket = null;
        }
        return false;
    }

    boolean SendCmd(byte[] cmd, Consumer<byte[]> consumer) {
        return SendCmd(cmd, consumer, 2);
    }

    boolean SendCmd(byte[] cmd) {
        return SendCmd(cmd, bytes -> {
        });
    }

    /**
     * 设置目标服务器的域名或者IP地址
     */
    public void SetDOMAIN_NAME(String domain_name) {
        if (domain_name != null && !domain_name.equals("")) {
            this.ip = domain_name;
        }
    }

    /**
     * 设置目标服务器端口
     */
    public void SetPORT(int port) {
        if (port > 0) {
            PORT = port;
        }
    }

    /**
     * 设置超时时间，默认为5秒
     */
    public void SetTIME_OUT(int time_out) {
        if (time_out > 0) {
            TIME_OUT = time_out;
        }
    }

}
