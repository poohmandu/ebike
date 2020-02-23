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

package com.qdigo.ebike.iotcenter.netty;

import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.iotcenter.config.NettyServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Socket Server main class.
 *
 * @author niezhao
 */
@Slf4j
@Component
public class SocketServer {

    @Resource(name = "bossGroup")
    private EventLoopGroup bossGroup;
    @Resource(name = "workerGroup")
    private EventLoopGroup workerGroup;
    @Resource
    private NettyServerProperties nettyServerProperties;
    @Resource
    private IotChildChannelInitializer iotChildChannelInitializer;

    private ChannelFuture serverChannelFuture;

    public final static String NET_IP = NetUtil.getIp();

    //@PostConstruct 用此注解会阻塞后续bean的实例化
    public void start() throws Exception {
        // 查看内存泄漏
        //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        //EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)  // 通过nio方式来接收连接和处理连接
        //EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap(); // (2)  // 引导辅助程序
        try {
            //设置线程组; 创建两个NioEventLoopGroup，一个是父线程（Boss线程),一个是子线程(work线程)
            b.group(bossGroup, workerGroup) //可链式设置
                    .channel(NioServerSocketChannel.class) // (3)  //设置nio类型的channel
                    //设置责任链路
                    //责任链模式是Netty的核心部分,每个处理者只负责自己有关的东西。然后将处理结果根据责任链传递下去
                    // (4)有连接到达时会创建一个channel
                    .childHandler(iotChildChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 1024) // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            // (7)  //配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            serverChannelFuture = b.bind(nettyServerProperties.getPort()).sync();

            log.info("netty server 已启动");

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to
            // gracefully
            // shut down your server.
            //这样导致springboot主线程阻塞，无法继续加载剩下的bean
            //serverChannelFuture.channel().closeFuture().sync();

        } finally {
            log.info("netty server被关闭");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    /**
     * 描述：关闭Netty 服务器，主要是释放连接
     *     连接包括：服务器连接serverChannel，
     *     客户端TCP处理连接bossGroup，
     *     客户端I/O操作连接workerGroup
     *
     *     若只使用
     *         bossGroupFuture = bossGroup.shutdownGracefully();
     *         workerGroupFuture = workerGroup.shutdownGracefully();
     *     会造成内存泄漏。
     */
    @PreDestroy
    public void stop() {
        log.info("正在释放netty server的资源");
        if (null == serverChannelFuture) {
            log.error("server channel is null");
        }
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (InterruptedException e) {
            log.error("销毁资源异常", e);
        }
    }
}