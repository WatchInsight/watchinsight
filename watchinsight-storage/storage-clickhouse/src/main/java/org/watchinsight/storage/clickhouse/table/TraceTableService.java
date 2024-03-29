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
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.storage.ITableService;

/**
 * @author Created by gerry
 * @date 2023-03-30-01:08
 */
@Slf4j
public class TraceTableService implements ITableService {
    
    private ClickHouseRequest<?> connect;
    
    public TraceTableService(ClickHouseRequest<?> connect) {
        this.connect = connect;
    }
    
    @Override
    public String keyPrefix() {
        return "trace";
    }
    
    @Override
    public void createTable(String tableName, int ttlDays, String sql) throws Exception {
        final String format = String.format(sql, tableName, ttlDays);
        connect.query(format).execute().get();
        log.info("Exec create traces table sql: {}", format);
    }
    
}
