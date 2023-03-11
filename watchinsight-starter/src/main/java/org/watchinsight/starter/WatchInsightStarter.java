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

package org.watchinsight.starter;

import java.io.FileNotFoundException;
import org.watchinsight.core.configuration.ApplicationConfigLoader;
import org.watchinsight.core.configuration.ApplicationConfiguration;
import org.watchinsight.core.configuration.ConfigLoader;
import org.watchinsight.core.module.DefaultModuleManager;
import org.watchinsight.core.module.ModuleManager;

/**
 * @author Created by gerry
 * @date 2023-03-05-22:59
 */
public class WatchInsightStarter {
    
    public static void main(String[] args) throws FileNotFoundException {
        //Build configuration loader
        final ConfigLoader<ApplicationConfiguration> configLoader = new ApplicationConfigLoader();
        //Load yml to application configuration
        final ApplicationConfiguration configuration = configLoader.load("application.yml");
        //Build module manager to init module & providers
        final ModuleManager manager = new DefaultModuleManager(configuration);
        manager.init();
    }
    
}
