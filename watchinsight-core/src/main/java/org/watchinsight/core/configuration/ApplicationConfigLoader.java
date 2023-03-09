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

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.ApplicationConfiguration.ProviderConfiguration;
import org.watchinsight.core.exception.ConfigurationException;
import org.watchinsight.core.utils.EmptyUtils;
import org.watchinsight.core.utils.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Created by gerry
 * @date 2023-03-08-23:33
 */
@Slf4j
public class ApplicationConfigLoader implements ConfigLoader<ApplicationConfiguration> {
    
    private final static String CHOICES = "choices";
    
    private Yaml yaml = new Yaml();
    
    @Override
    public ApplicationConfiguration load(final String fileName) throws FileNotFoundException {
        ApplicationConfiguration configuration = new ApplicationConfiguration();
        this.loadConfiguration(fileName, configuration);
        return configuration;
    }
    
    private void loadConfiguration(String fileName, ApplicationConfiguration configuration)
        throws FileNotFoundException {
        //Load yml file to config
        final Reader reader = ResourceUtils.read(fileName);
        final Map<String, Map<String, Object>> config = yaml.loadAs(reader, Map.class);
        if (EmptyUtils.isEmpty(config)) {
            throw new ConfigurationException("Load file [" + fileName + "] configuration is null");
        }
        //Load config to ModuleConfiguration
        config.forEach((module, providers) -> configuration.addModule(module, loadProviders(config.get(module))));
    }
    
    private List<ProviderConfiguration> loadProviders(final Map<String, Object> config) {
        final List<String> choices = (ArrayList<String>) config.get(CHOICES);
        final List<ProviderConfiguration> providers = new ArrayList<>(choices.size());
        for (String choice : choices) {
            ProviderConfiguration providerConfiguration = choice(choice, config);
            if (Objects.isNull(providerConfiguration)) {
                continue;
            }
            providers.add(providerConfiguration);
        }
        return providers;
    }
    
    private ProviderConfiguration choice(final String choice, final Map<String, Object> config) {
        final Map<String, ?> propertyConfig = (Map<String, ?>) config.get(choice);
        if (EmptyUtils.isEmpty(propertyConfig)) {
            log.warn("Choice module's provider [{}], but no define, ignore.", choice);
            return null;
        }
        final Properties properties = new Properties();
        propertyConfig.forEach((propertyName, propertyValue) -> {
            if (propertyValue instanceof Map) {
                Properties subProperties = new Properties();
                ((Map) propertyValue).forEach((key, value) -> {
                    subProperties.put(key, value);
                });
                properties.put(propertyName, subProperties);
            } else {
                properties.put(propertyName, propertyValue);
            }
        });
        return new ProviderConfiguration(choice, properties);
    }
}
