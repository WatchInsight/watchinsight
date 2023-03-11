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

package org.watchinsight.core.module;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.ApplicationConfiguration;
import org.watchinsight.core.configuration.ApplicationConfiguration.ModuleConfiguration;
import org.watchinsight.core.exception.ProviderNotFoundException;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.service.ServiceDefine;

/**
 * @author Created by gerry
 * @date 2023-03-10-22:54
 */
@Slf4j
public class DefaultModuleManager implements ModuleManager {
    
    private ApplicationConfiguration configuration;
    
    private Map<ModuleDefine, List<ProviderDefine>> moduleDefines = Maps.newConcurrentMap();
    
    public DefaultModuleManager(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Init module
     */
    public synchronized void init() {
        final ServiceLoader<ModuleDefine> moduleDefines = ServiceLoader.load(ModuleDefine.class);
        final Set<String> modules = configuration.modules();
        for (ModuleDefine define : moduleDefines) {
            final String module = define.module();
            if (!modules.contains(module)) {
                log.warn("[{}] module is undefined, ignore.", define.module());
                continue;
            }
            final ModuleConfiguration moduleConfiguration = configuration.getModuleConfiguration(module);
            final ServiceLoader<ProviderDefine> providerDefines = ServiceLoader.load(ProviderDefine.class);
            //prepare
            final List<ProviderDefine> providers = define.prepare(this, moduleConfiguration, providerDefines);
            this.moduleDefines.put(define, providers);
        }
        //start
        start();
        //after
        after();
        //stop
        stop();
    }
    
    private void start() {
        for (List<ProviderDefine> providerDefine : moduleDefines.values()) {
            for (ProviderDefine provider : providerDefine) {
                provider.start();
            }
        }
    }
    
    private void after() {
        for (List<ProviderDefine> providerDefine : moduleDefines.values()) {
            for (ProviderDefine provider : providerDefine) {
                provider.after();
            }
        }
    }
    
    public void stop() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (List<ProviderDefine> providerDefine : moduleDefines.values()) {
                for (ProviderDefine provider : providerDefine) {
                    provider.stop();
                }
            }
        }));
    }
    
    @Override
    public boolean has(String module) {
        return Objects.nonNull(configuration.getModuleConfiguration(module));
    }
    
    @Override
    public ServiceDefine find(String module, String provider) {
        for (ModuleDefine moduleDefine : moduleDefines.keySet()) {
            if (!moduleDefine.module().equals(module)) {
                continue;
            }
            for (ProviderDefine providerDefine : moduleDefines.get(moduleDefine)) {
                if (providerDefine.name().equals(provider)) {
                    return providerDefine;
                }
            }
        }
        throw new ProviderNotFoundException("Module [" + module + "] & provider [" + provider + "] not found.");
    }
    
}
