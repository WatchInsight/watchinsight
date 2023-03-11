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

package org.watchinsight.core.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.watchinsight.core.exception.ServiceNotFoundException;
import org.watchinsight.core.module.ModuleManager;
import org.watchinsight.core.service.ServiceDefine;
import org.watchinsight.core.utils.EmptyUtils;

/**
 * @author Created by gerry
 * @date 2023-03-10-23:20
 */
public abstract class ProviderDefine implements ServiceDefine {
    
    private final Map<Class<? extends ServiceDefine>, ServiceDefine> services = new ConcurrentHashMap<>();
    
    @Getter
    private ModuleManager moduleManager;
    
    public void addModuleManager(final ModuleManager manager) {
        this.moduleManager = manager;
    }
    
    /**
     * Provider's name
     *
     * @return
     */
    public abstract String name();
    
    /**
     * Create config
     *
     * @param <T>
     * @return
     */
    public abstract <T extends ProviderConfig> T createConfig();
    
    /**
     * Provider prepare
     */
    public abstract void prepare();
    
    /**
     * Provider start
     */
    public abstract void start();
    
    /**
     * Provider after
     */
    public abstract void after();
    
    /**
     * Provider stop
     */
    public abstract void stop();
    
    public ServiceDefine find(String module, String provider){
        return moduleManager.find(module, provider);
    }
    
    @Override
    public void register(Class<? extends ServiceDefine> serviceType, ServiceDefine service)
        throws ServiceNotFoundException {
        if (serviceType.isInstance(service)) {
            this.services.put(serviceType, service);
        } else {
            throw new ServiceNotFoundException(serviceType + " is not implemented by " + service);
        }
        
    }
    
    @Override
    public <T extends ServiceDefine> T getService(Class<T> clazz) {
        ServiceDefine service = services.get(clazz);
        if (EmptyUtils.isNotEmpty(service)) {
            return (T) service;
        }
        throw new ServiceNotFoundException(
            "Service " + clazz.getName() + " should not be provided, based on provider " + name() + ".");
    }
}
