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

import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.watchinsight.core.storage.ITableService;
import org.watchinsight.core.utils.EmptyUtils;
import org.watchinsight.core.utils.ResourceUtils;
import org.watchinsight.storage.clickhouse.ClickHouseConfig;
import org.watchinsight.storage.clickhouse.exception.TableCreateException;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Created by gerry
 * @date 2023-03-31-00:24
 */
public class DefaultTableServiceManager implements TableServiceManager {
    
    private ClickHouseConfig config;
    private List<ITableService> tableServices;
    private Map<String, String> ymls = Maps.newHashMap();
    private Yaml yaml = new Yaml();
    
    public DefaultTableServiceManager(ClickHouseConfig config, List<ITableService> tableServices) {
        this.config = config;
        this.tableServices = tableServices;
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
                    throw new TableCreateException(
                        "Storage module clickHouse provider's config file not exist prefix is [" + keyPrefix
                            + "] table name");
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
            throw new TableCreateException(e.getMessage());
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
