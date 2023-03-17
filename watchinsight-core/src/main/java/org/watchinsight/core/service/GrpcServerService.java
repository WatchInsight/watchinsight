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

import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
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
public class GrpcServerService implements IServerService, ServerInterceptor {
    
    private GrpcProviderConfig config;
    
    private Server server;
    
    public GrpcServerService(GrpcProviderConfig config) {
        this.config = config;
    }
    
    private static final Metadata.Key<String> AUTH_HEADER_NAME = Metadata.Key
        .of("Authentication", Metadata.ASCII_STRING_MARSHALLER);
    
    @Override
    public void start() throws Exception {
        //TODO Need support NettyServerBuilder.addService
        this.server = NettyServerBuilder.forPort(config.getPort())
            .bossEventLoopGroup(new NioEventLoopGroup(config.getWorkThreads()))
            .workerEventLoopGroup(new NioEventLoopGroup())
            .intercept(this)
            .channelType(NioServerSocketChannel.class).build();
        this.server.start();
    }
    
    @Override
    public void shutdown() throws Exception {
        server.shutdown();
        server.awaitTermination();
    }
    
    @Override
    public <REQUEST, RESPONSE> Listener<REQUEST> interceptCall(ServerCall<REQUEST, RESPONSE> call, Metadata headers,
        ServerCallHandler<REQUEST, RESPONSE> next) {
        final String auth = headers.get(AUTH_HEADER_NAME);
        if (auth.equals(config.getToken())) {
            return next.startCall(call, headers);
        }
        call.close(Status.PERMISSION_DENIED, new Metadata());
        return new ServerCall.Listener<REQUEST>() {
        };
    }
}
