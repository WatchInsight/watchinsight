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

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.GrpcProviderConfig;
import org.watchinsight.core.exception.ModuleStartException;
import org.watchinsight.core.exception.ModuleStopException;
import org.watchinsight.core.provider.ProviderDefine;

/**
 * @author Created by gerry
 * @date 2023-03-10-23:22
 */
@Slf4j
public class CoreGprcProvider extends ProviderDefine {
    
    public static final String GRPC = "grpc";
    
    private GrpcProviderConfig config;
    
    private Server server;
    
    public CoreGprcProvider() {
        this.config = new GrpcProviderConfig();
    }
    
    @Override
    public String name() {
        return GRPC;
    }
    
    @Override
    public GrpcProviderConfig createConfig() {
        return this.config;
    }
    
    @Override
    public void prepare() {
        final Executor executor = new ThreadPoolExecutor(
            config.getThreadPoolSize(), config.getThreadPoolSize(), 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(config.getThreadPoolQueueSize()),
            new DefaultThreadFactory("GrpcServerPool"),
            (r, e) -> log.error("Grpc server thread pool is full, reject all task."));
        this.server = NettyServerBuilder.forPort(config.getPort())
            .maxConcurrentCallsPerConnection(config.getMaxConcurrentCallsPerConnection())
            .maxInboundMessageSize(config.getMaxInboundMessageSize()).executor(executor).build();
    }
    
    @Override
    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new ModuleStartException(e.getMessage(), e);
        }
    }
    
    @Override
    public void after() {
    }
    
    @Override
    public void stop() {
        try {
            server.shutdown();
        } catch (Exception e) {
            throw new ModuleStopException(e.getMessage(), e);
        }
    }
    
}
