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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.ApplicationConfiguration.ModuleConfiguration;
import org.watchinsight.core.configuration.ApplicationConfiguration.ProviderConfiguration;
import org.watchinsight.core.exception.ProviderConfigException;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.provider.ProviderConfig;
import org.watchinsight.core.utils.EmptyUtils;

/**
 * @author Created by gerry
 * @date 2023-03-08-22:35
 */
@Slf4j
public abstract class ModuleDefine {
    
    public abstract String module();
    
    public List<ProviderDefine> prepare(ModuleManager manager, ModuleConfiguration moduleConfiguration,
        ServiceLoader<ProviderDefine> providerDefines) {
        final List<ProviderDefine> providers = new ArrayList<>();
        for (ProviderConfiguration provider : moduleConfiguration.getProviders()) {
            final ProviderDefine providerDefine = getProviderDefine(moduleConfiguration.getName(), provider.getName(),
                providerDefines);
            if (Objects.isNull(providerDefine)) {
                continue;
            }
            //Init config
            final ProviderConfig config = providerDefine.createConfig();
            try {
                //Invoke prepare
                prepare(providerDefine, provider.getProperties(), config);
                providerDefine.addModuleManager(manager);
                providers.add(providerDefine);
            } catch (IllegalAccessException e) {
                throw new ProviderConfigException(
                    "Provider [" + provider + "] config transport to config bean failure.", e);
            }
        }
        return providers;
    }
    
    private ProviderDefine getProviderDefine(final String module, final String provider,
        final ServiceLoader<ProviderDefine> providerDefines) {
        if (EmptyUtils.isEmpty(provider)) {
            return null;
        }
        for (ProviderDefine providerDefine : providerDefines) {
            if (module.equals(providerDefine.module()) && provider.equals(providerDefine.name())) {
                return providerDefine;
            }
        }
        return null;
    }
    
    private void prepare(ProviderDefine providerDefine, Properties properties, ProviderConfig config)
        throws IllegalAccessException {
        copyProperties(properties, config, providerDefine.name());
        providerDefine.prepare();
    }
    
    private void copyProperties(Properties src, ProviderConfig dest, String provider) throws IllegalAccessException {
        if (dest == null) {
            return;
        }
        Enumeration<?> propertyNames = src.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            Class<? extends ProviderConfig> destClass = dest.getClass();
            try {
                Field field = getDeclaredField(destClass, propertyName);
                field.setAccessible(true);
                field.set(dest, src.get(propertyName));
            } catch (NoSuchFieldException e) {
                log.warn(
                    e.getMessage() + " ,propertyName [" + propertyName + "] setting is not supported in [" + provider
                        + "] provider");
            }
        }
    }
    
    private Field getDeclaredField(Class<?> destClass, String fieldName) throws NoSuchFieldException {
        if (destClass != null) {
            Field[] fields = destClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            return getDeclaredField(destClass.getSuperclass(), fieldName);
        }
        throw new NoSuchFieldException();
    }
    
}
