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

package org.watchinsight.storage.clickhouse;

import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseResponse;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.exception.ModuleStartException;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.storage.StorageModule;
import org.watchinsight.storage.clickhouse.service.ClickHouseService;
import org.watchinsight.storage.clickhouse.service.IClickHouseService;

/**
 * @author Created by gerry
 * @date 2023-03-23-23:43
 */
@Slf4j
public class ClickHouseProvider extends ProviderDefine {
    
    public static final String CLICKHOUSE = "clickhouse";
    
    private ClickHouseConfig config;
    
    private ClickHouseRequest<?> connect;
    
    public ClickHouseProvider() {
        this.config = new ClickHouseConfig();
    }
    
    @Override
    public String name() {
        return CLICKHOUSE;
    }
    
    @Override
    public ClickHouseConfig createConfig() {
        return this.config;
    }
    
    @Override
    public void prepare() {
        final ClickHouseService clickHouseService = new ClickHouseService(config);
        super.register(IClickHouseService.class, clickHouseService);
        clickHouseService.createServer();
    }
    
    @Override
    public void start() {
        final IClickHouseService service = super.getService(IClickHouseService.class);
        this.connect = service.getConnect();
    }
    
    @Override
    public void after() {
        try {
            final ClickHouseResponse response = this.connect.query("drop table if exists watchinsight_traces").execute().get();
            log.info(response.stream().count() + " 返回");
        } catch (Exception e) {
            throw new ModuleStartException(e.getMessage(), e);
        }
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public String module() {
        return StorageModule.STORAGE;
    }
}
