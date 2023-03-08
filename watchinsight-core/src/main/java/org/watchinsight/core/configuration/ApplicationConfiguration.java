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

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Created by gerry
 * @date 2023-03-08-23:22
 */
public class ApplicationConfiguration {
    
    private Map<String, ModuleConfiguration> modules = Maps.newConcurrentMap();
    
    public void addModule(final String moduleName, final ModuleConfiguration configuration) {
    }
    
    /**
     * The configuration about a module.
     */
    public static class ModuleConfiguration {
        
        private Map<String, List<ProviderConfiguration>> providers = Maps.newConcurrentMap();
    }
    
    /**
     * The configuration about some providers for a module.
     */
    public static class ProviderConfiguration {
        
        private String name;
        
        private Properties properties;
    }
    
}
