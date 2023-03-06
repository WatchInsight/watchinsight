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

import org.yaml.snakeyaml.Yaml;

/**
 * From yml file load resource
 *
 * @author Created by gerry
 * @date 2023-03-06-23:33
 */
public class ModuleResourceLoader implements ResourceLoader {
    
    private String fileName;
    
    private Yaml yaml;
    
    public ModuleResourceLoader(final String fileName) {
        this.fileName = fileName;
        this.yaml = new Yaml();
    }
    /**
     * 1、读取目标路径的配置文件
     * 2、解析配置文件，转换成可操作的ModuleDefinition对象
     * 3、通过ModuleDefinition可获得配置文件中配置的相关模块
     * 4、通过ModuleFactory可对ModuleDefinition进行初始化
     */
    
}
