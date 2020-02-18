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

import com.qdigo.ebike.iotcenter.handler.GSMDataDecoder;
import com.qdigo.ebike.iotcenter.handler.ParseBytesHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Socket Server main class.
 *
 * @author niezhao
 */
@Slf4j
public class SocketServer {

    public static final int PORT = 13078;
    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZTHREADSIZE = 4;

    //连接处理group
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    //事件处理group
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    public static String NET_IP = "";

    private ChannelFuture serverChannelFuture;


    //@PostConstruct 用此注解会阻塞后续bean的实例化
    public void start() throws Exception {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        //EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)  // 通过nio方式来接收连接和处理连接
        //EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap(); // (2)  // 引导辅助程序
        try {
            //设置线程组; 创建两个NioEventLoopGroup，一个是父线程（Boss线程),一个是子线程(work线程)
            b.group(bossGroup, workerGroup) //可链式设置
                    .channel(NioServerSocketChannel.class) // (3)  //设置nio类型的channel
                    //设置责任链路
                    //责任链模式是Netty的核心部分,每个处理者只负责自己有关的东西。然后将处理结果根据责任链传递下去
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)  //有连接到达时会创建一个channel
                        @Override
                        protected void initChannel(SocketChannel ch) {

                            //pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                            ByteBuf delimiter_$ = Unpooled.copiedBuffer("$".getBytes());
                            ByteBuf delimiter_$_ = Unpooled.copiedBuffer("$_".getBytes());
                            DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(1024, delimiter_$);
                            //DelimiterBasedFrameDecoder默认会去掉分隔符
                            ch.pipeline()
                                    //.addLast(new FixedLengthFrameDecoder(30))
                                    .addLast(new GSMDataDecoder())
                                    .addLast(new ParseBytesHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024) // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(PORT).sync(); // (7)  //配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            channel = f.channel();
            SocketServer.NET_IP = SocketServer.getIp();
            logger.info("server netip :" + SocketServer.NET_IP);
            logger.info("server is running on port :" + SocketServer.PORT);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to
            // gracefully
            // shut down your server.

            f.channel().closeFuture().sync();

        } finally {
            logger.info("server socket is closed");
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

    /**
     * get net ip of current server.
     *
     * @return ip address
     * @throws SocketException
     */
    private static String getIp() throws SocketException {

        String localip = null; // 本地IP，如果没有配置外网IP则返回它
        String netip = null; // 外网IP

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        boolean finded = false;// 是否找到外网IP

        while (netInterfaces.hasMoreElements() && !finded) {

            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();

            while (address.hasMoreElements()) {

                ip = address.nextElement();

                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) { // 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && !ip.getHostAddress().contains(":")) { // 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }
}