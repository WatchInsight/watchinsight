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

package org.watchinsight.receiver.oltp.test;

import io.opentelemetry.proto.trace.v1.Span;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.watchinsight.core.configuration.ApplicationConfigLoader;
import org.watchinsight.core.configuration.ApplicationConfiguration;
import org.watchinsight.core.configuration.ConfigLoader;
import org.watchinsight.core.module.DefaultModuleManager;

/**
 * @author Created by gerry
 * @date 2023-03-23-23:21
 */
public class ReceiverOtlpTest {
    
    private IGprcExampleService gprcExampleService;
    
    private Span span;
    
    @Before
    public void start() throws FileNotFoundException {
        final ConfigLoader<ApplicationConfiguration> configLoader = new ApplicationConfigLoader();
        //Load yml to application configuration
        final ApplicationConfiguration configuration = configLoader.load("application.yml");
        //Build module manager to init module & providers
        final DefaultModuleManager manager = new DefaultModuleManager(configuration);
        manager.init();
        this.gprcExampleService = new GrpcExampleService("127.0.0.1", 19091, "123456");
        this.span = gprcExampleService.newSpans();
        gprcExampleService.newChannel();
    }
    
    @Test
    public void export(){
        this.gprcExampleService.export(span);
    }
}
