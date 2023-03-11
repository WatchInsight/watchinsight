/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.watchinsight.core;

import io.grpc.netty.shaded.io.netty.bootstrap.ServerBootstrap;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.netty.shaded.io.netty.channel.ChannelInitializer;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import io.grpc.netty.shaded.io.netty.channel.EventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.SocketChannel;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpObjectAggregator;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpServerCodec;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.HttpProviderConfig;
import org.watchinsight.core.provider.ProviderDefine;

/**
 * @author Created by gerry
 * @date 2023-03-10-23:22
 */
@Slf4j
public class CoreHttpProvider extends ProviderDefine {
    
    public static final String HTTP = "http";
    
    private HttpProviderConfig providerConfig;
    
    private Channel channel;
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    public CoreHttpProvider() {
        this.providerConfig = new HttpProviderConfig();
    }
    
    @Override
    public String name() {
        return HTTP;
    }
    
    @Override
    public HttpProviderConfig createConfig() {
        return this.providerConfig;
    }
    
    @Override
    public void prepare() {
        System.out.println("http provider prepare.");
    }
    
    @Override
    public void start() {
        bossGroup = new NioEventLoopGroup(providerConfig.getWorkThreads());
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap().option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                        .addLast(new HttpServerExpectContinueHandler());
                }
            });
        this.channel = b.bind(providerConfig.getPort()).channel();
        log.info("Netty http server listening on port " + providerConfig.getPort());
    }
    
    @Override
    public void after() {
        System.out.println("http provider after.");
    }
    
    @Override
    public void stop() {
        log.info("Netty HTTP Channel closed!");
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
    
}
