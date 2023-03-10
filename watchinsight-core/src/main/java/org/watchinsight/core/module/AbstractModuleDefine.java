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

import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.ApplicationConfiguration.ModuleConfiguration;
import org.watchinsight.core.provider.ProviderDefine;

/**
 * @author Created by gerry
 * @date 2023-03-08-22:35
 */
@Slf4j
public abstract class AbstractModuleDefine implements ModuleDefine {
    
    @Override
    public void prepare(ModuleManager manager, ModuleConfiguration moduleConfiguration,
        ServiceLoader<ProviderDefine> providerDefines) {
        for (ProviderDefine providerDefine : providerDefines) {
            final String provider = providerDefine.name();
            if (!moduleConfiguration.has(provider)) {
                log.warn("[{}] provider is undefined, ignore.", provider);
                continue;
            }
            //Init config
            providerDefine.prepare();
        }
    }
}
