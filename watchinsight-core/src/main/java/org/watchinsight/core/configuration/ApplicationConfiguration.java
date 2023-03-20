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

package org.watchinsight.core.configuration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.exception.ConfigurationException;
import org.watchinsight.core.utils.EmptyUtils;

/**
 * @author Created by gerry
 * @date 2023-03-08-23:22
 */
@Slf4j
public class ApplicationConfiguration {
    
    private Map<String, ModuleConfiguration> modules = Maps.newConcurrentMap();
    
    public ModuleConfiguration getModuleConfiguration(final String module) {
        return modules.get(module);
    }
    
    public Set<String> modules() {
        return modules.keySet();
    }
    
    public void addModule(final String module, final List<ProviderConfiguration> providers) {
        if (EmptyUtils.isEmpty(providers)) {
            log.warn("Load [{}] module define, but no providers, ignore.", module);
            return;
        }
        if (modules.containsKey(module)) {
            throw new ConfigurationException("Add module [" + module + "] duplicate, please check yml file.");
        }
        final ModuleConfiguration moduleConfiguration = new ModuleConfiguration(module);
        for (ProviderConfiguration provider : providers) {
            moduleConfiguration.addProvider(provider);
        }
        log.info("Load module [{}], providers for {}", module,
            providers.stream().map(ProviderConfiguration::getName).collect(Collectors.toList()));
        modules.put(module, moduleConfiguration);
    }
    
    /**
     * The configuration about a module.
     */
    public static class ModuleConfiguration {
        
        @Getter
        private String name;
    
        /**
         * The a module for n providers
         */
        private List<ProviderConfiguration> providers = Lists.newArrayList();
        
        public ModuleConfiguration(String name) {
            this.name = name;
        }
        
        public void addProvider(ProviderConfiguration configuration) {
            if (Objects.isNull(configuration)) {
                log.warn("Add a provider fail for is null.");
                return;
            }
            final ProviderConfiguration providerConfiguration = providers.stream()
                .filter(provider -> provider.getName().equals(configuration.getName())).findFirst()
                .orElse(null);
            if (Objects.nonNull(providerConfiguration)) {
                throw new ConfigurationException(
                    "Add provider [" + configuration.getName() + "] duplicate, please check yml file.");
            }
            providers.add(configuration);
        }
        
        public ProviderConfiguration find(String provider) {
            return providers.stream().filter(_provider -> _provider.getName().equals(provider)).findFirst()
                .orElse(null);
        }
        
        public boolean has(String provider) {
            return providers.stream().filter(_provider -> _provider.getName().equals(provider)).findAny().isPresent();
        }
    }
    
    /**
     * The configuration about some providers for a module.
     */
    @Getter
    public static class ProviderConfiguration {
        
        private String name;
        
        private Properties properties;
        
        public ProviderConfiguration(String name, Properties properties) {
            this.name = name;
            this.properties = properties;
        }
    }
    
}
