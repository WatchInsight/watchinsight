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
import com.clickhouse.client.ClickHouseRequest.Mutation;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseDataStreamFactory;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHousePipedOutputStream;
import com.clickhouse.data.format.BinaryStreamUtils;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.core.storage.ITableService;

/**
 * @author Created by gerry
 * @date 2023-04-03-23:56
 */
@Slf4j
public abstract class AbstractTableService implements ITableService {
    
    private ClickHouseRequest<?> client;
    
    public AbstractTableService(ClickHouseRequest<?> client) {
        this.client = client;
    }
    
    @Override
    public void createTable(String tableName, int ttlDays, String sql) throws Exception {
        final String format = MessageFormat.format(sql, tableName, ttlDays);
        client.query(format).execute().get();
        log.info("Exec create traces table sql: {}", format);
    }
    
    @Override
    public void insertTable(String tableName, String sql) throws Exception {
        final Mutation format = client.write().table(tableName).format(ClickHouseFormat.RowBinary);
        final ClickHousePipedOutputStream stream = ClickHouseDataStreamFactory.getInstance()
            .createPipedOutputStream(format.getConfig());
        final CompletableFuture<ClickHouseResponse> future = format.data(stream.getInputStream()).execute();
        //TODO Need param is records for lines and values
        for (Integer integer : Lists.newArrayList(1000)) {
            BinaryStreamUtils.writeString(stream, String.valueOf("x"));
        }
        
    }
}
