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

package org.watchinsight.core.service;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.GrpcProviderConfig;

/**
 * @author Created by gerry
 * @date 2023-03-12-23:49
 */
@Slf4j
public class GrpcServerService implements IServerService {
    
    private GrpcProviderConfig config;
    
    private Server server;
    
    public GrpcServerService(GrpcProviderConfig config) {
        this.config = config;
    }
    
    @Override
    public void start() throws Exception {
        //TODO Need support grpc auth interceptor & NettyServerBuilder.addService
        this.server = NettyServerBuilder.forPort(config.getPort())
            .bossEventLoopGroup(new NioEventLoopGroup(config.getWorkThreads()))
            .workerEventLoopGroup(new NioEventLoopGroup())
            .channelType(NioServerSocketChannel.class).build();
        this.server.start();
    }
    
    @Override
    public void shutdown() throws Exception {
        server.shutdown();
        server.awaitTermination();
    }
    
}
