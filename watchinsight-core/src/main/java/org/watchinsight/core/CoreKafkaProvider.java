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

import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.provider.ProviderConfig;

/**
 * @author Created by gerry
 * @date 2023-03-10-23:22
 */
public class CoreKafkaProvider extends ProviderDefine {
    
    public static final String KAFKA = "kafka";
    
    @Override
    public String name() {
        return KAFKA;
    }
    
    @Override
    public <T extends ProviderConfig> T createConfig() {
        return null;
    }
    
    @Override
    public void prepare() {
        System.out.println("kafka provider prepare.");
    }
    
    @Override
    public void start() {
        System.out.println("kafka provider started.");
    }
    
    @Override
    public void after() {
        System.out.println("kafka provider after.");
    }
    
    @Override
    public void stop() {
        System.out.println("kafka provider stopping.");
    }
    
}
