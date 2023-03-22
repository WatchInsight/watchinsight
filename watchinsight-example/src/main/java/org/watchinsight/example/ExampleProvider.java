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

package org.watchinsight.example;

import io.opentelemetry.proto.trace.v1.Span;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.example.grpc.GrpcExampleService;
import org.watchinsight.example.grpc.IGprcExampleService;

/**
 * @author Created by gerry
 * @date 2023-03-19-23:17
 */
public class ExampleProvider extends ProviderDefine {
    
    public static final String GRPC = "grpc";
    
    private ExampleConfig config;
    
    private Span span;
    
    public ExampleProvider() {
        this.config = new ExampleConfig();
    }
    
    @Override
    public String name() {
        return GRPC;
    }
    
    @Override
    public ExampleConfig createConfig() {
        return this.config;
    }
    
    @Override
    public void prepare() {
        super.register(IGprcExampleService.class,
            new GrpcExampleService(config.getGrpcHost(), config.getGrpcPort(), config.getToken()));
    }
    
    @Override
    public void start() {
        super.getService(IGprcExampleService.class).newChannel();
        this.span = super.getService(IGprcExampleService.class).newSpans();
    }
    
    @Override
    public void after() {
        super.getService(IGprcExampleService.class).export(span);
    }
    
    @Override
    public void stop() {
        super.getService(IGprcExampleService.class).stop();
    }
    
    @Override
    public String module() {
        return ExampleModule.EXAMPLE;
    }
}
