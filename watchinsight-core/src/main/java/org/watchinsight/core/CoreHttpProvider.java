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

import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.HttpProviderConfig;
import org.watchinsight.core.exception.ModuleStartException;
import org.watchinsight.core.exception.ModuleStopException;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.service.HttpServerService;
import org.watchinsight.core.service.IServerService;

/**
 * @author Created by gerry
 * @date 2023-03-10-23:22
 */
@Slf4j
public class CoreHttpProvider extends ProviderDefine {
    
    public static final String HTTP = "http";
    
    private HttpProviderConfig config;
    
    public CoreHttpProvider() {
        this.config = new HttpProviderConfig();
    }
    
    @Override
    public String name() {
        return HTTP;
    }
    
    @Override
    public HttpProviderConfig createConfig() {
        return this.config;
    }
    
    @Override
    public void prepare() {
        super.register(IServerService.class, new HttpServerService(config));
    }
    
    @Override
    public void start() {
        try {
            super.getService(IServerService.class).start();
            log.info("Netty http server listening on port " + config.getPort());
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
            log.info("Netty HTTP Channel stopping!");
            super.getService(IServerService.class).shutdown();
        } catch (Exception e) {
            throw new ModuleStopException(e.getMessage(), e);
        }
    }
    
}
