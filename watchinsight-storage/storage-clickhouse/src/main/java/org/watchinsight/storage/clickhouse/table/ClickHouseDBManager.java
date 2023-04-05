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

package org.watchinsight.storage.clickhouse.table;

import com.clickhouse.client.ClickHouseRequest;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.exception.DBCreateException;
import org.watchinsight.core.storage.IDBService;
import org.watchinsight.core.storage.ITableService;
import org.watchinsight.core.utils.EmptyUtils;
import org.watchinsight.core.utils.ResourceUtils;
import org.watchinsight.storage.clickhouse.ClickHouseConfig;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Created by gerry
 * @date 2023-03-31-00:24
 */
@Slf4j
public class ClickHouseDBManager implements IDBService {
    
    private ClickHouseConfig config;
    
    private ClickHouseRequest<?> client;
    
    private List<ITableService> tableServices;
    
    private Map<String, String> ymls = Maps.newHashMap();
    
    private Yaml yaml = new Yaml();
    
    public ClickHouseDBManager(ClickHouseConfig config, ClickHouseRequest<?> client,
        List<ITableService> tableServices) {
        this.config = config;
        this.client = client;
        this.tableServices = tableServices;
    }
    
    @Override
    public void createDatabase(String database) {
        try {
            final String createSql = String.format("CREATE DATABASE IF NOT EXISTS %s;", database);
            client.query(createSql).execute().get();
            log.info("Exec create database sql: {}", createSql);
            final String useSql = String.format("use %s;", database);
            client.query(useSql).execute().get();
            log.info("Exec use database sql: {}", useSql);
        } catch (Exception e) {
            throw new DBCreateException(e.getMessage());
        }
    }
    
    @Override
    public void createTables() throws FileNotFoundException {
        read();
        try {
            for (ITableService tableService : tableServices) {
                //TableService's keyPrefix
                final String keyPrefix = tableService.keyPrefix();
                final String tableName = config.getTables().stream().filter(t -> t.toLowerCase().contains(keyPrefix))
                    .findFirst()
                    .orElse(null);
                if (EmptyUtils.isEmpty(tableName)) {
                    log.warn(
                        "Storage module clickHouse provider's config file not exist prefix is [{}] table name, it's ignored.",
                        keyPrefix);
                    continue;
                }
                final List<String> keys = ymls.keySet().stream().filter(t -> t.toLowerCase().contains(keyPrefix))
                    .collect(Collectors.toList());
                if (EmptyUtils.isEmpty(keys)) {
                    continue;
                }
                for (String key : keys) {
                    tableService.createTable(tableName, config.getTtlDays(), ymls.get(key));
                }
            }
        } catch (Exception e) {
            throw new DBCreateException(e.getMessage());
        }
        
    }
    
    private void read() throws FileNotFoundException {
        if (EmptyUtils.isNotEmpty(ymls)) {
            return;
        }
        final Reader read = ResourceUtils.read("table.yml");
        this.ymls = yaml.loadAs(read, Map.class);
    }
}
