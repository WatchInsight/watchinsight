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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.configuration.ApplicationConfiguration.ModuleConfiguration;
import org.watchinsight.core.utils.EmptyUtils;
import org.watchinsight.core.utils.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Created by gerry
 * @date 2023-03-08-23:33
 */
@Slf4j
public class ApplicationConfigLoader implements ConfigLoader<ApplicationConfiguration> {
    
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
            log.warn("Load file [{}] configuration is null", fileName);
            return;
        }
        //Load config to ModuleConfiguration
        loadModule(config, configuration);
        /**
         * 1、读取目标路径的配置文件
         * 2、解析配置文件，转换成可操作的ModuleDefine对象
         * 3、通过ModuleDefine可获得配置文件中配置的相关模块
         * 4、通过ModuleFactory可对ModuleDefine进行初始化
         */
    }
    
    //{"core":{"choices":["aaa","bbb"],"aaa":{"k1":"v1"}, "bbb":{"k1":"v1"}}}
    private void loadModule(final Map<String, Map<String, Object>> config,
        final ApplicationConfiguration configuration) {
        for (String moduleName : config.keySet()) {
            ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
//            configuration.addModule(moduleName, config.get(moduleName));
        }
    }
    
    private void loadModule(final Map<String, Object> modules) {
    }
    
    private void loadProvider(final String moduleName) {
    }
}
