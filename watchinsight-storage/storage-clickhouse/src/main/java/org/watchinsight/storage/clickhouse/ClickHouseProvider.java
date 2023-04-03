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
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.exception.ModuleStartException;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.storage.StorageModule;
import org.watchinsight.storage.clickhouse.service.ClickHouseService;
import org.watchinsight.storage.clickhouse.service.IClickHouseService;
import org.watchinsight.storage.clickhouse.table.ClickHouseDBManager;
import org.watchinsight.storage.clickhouse.table.DBManager;
import org.watchinsight.storage.clickhouse.table.TraceTableService;

/**
 * @author Created by gerry
 * @date 2023-03-23-23:43
 */
@Slf4j
public class ClickHouseProvider extends ProviderDefine {
    
    public static final String CLICKHOUSE = "clickhouse";
    
    private ClickHouseConfig config;
    
    private ClickHouseRequest<?> client;
    
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
        //create clickhouse server
        clickHouseService.createServer();
        //get clickhouse client
        this.client = clickHouseService.getConnect();
        super.register(IClickHouseService.class, clickHouseService);
        final TraceTableService traceTableService = new TraceTableService(client);
        super.register(TraceTableService.class, traceTableService);
        super.register(DBManager.class,
            new ClickHouseDBManager(config, client, Lists.newArrayList(traceTableService)));
    }
    
    @Override
    public void start() {
        try {
            final DBManager dbManager = super.getService(DBManager.class);
            dbManager.createDatabase(config.getDatabase());
            dbManager.createTables();
        } catch (Exception e) {
            throw new ModuleStartException(e.getMessage());
        }
    }
    
    @Override
    public void after() {
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public String module() {
        return StorageModule.STORAGE;
    }
}
