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

package org.watchinsight.storage.clickhouse.service;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseFormat;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.watchinsight.storage.clickhouse.ClickHouseConfig;

/**
 * @author Created by gerry
 * @date 2023-03-25-20:48
 */
@Slf4j
public class ClickHouseService implements IClickHouseService {
    
    private ClickHouseConfig config;
    
    private ClickHouseNode server;
    
    private ClickHouseRequest<?> request;
    
    public ClickHouseService(ClickHouseConfig config) {
        this.config = config;
    }
    
    @Override
    public void createServer() {
        this.server = ClickHouseNode.builder().host(config.getUrl()).port(ClickHouseProtocol.GRPC, config.getPort())
            .database(config.getDatabase())
            .credentials(ClickHouseCredentials.fromUserAndPassword(config.getUser(), config.getPassword())).build();
    }
    
    @Override
    public ClickHouseRequest<?> getConnect() {
        if (Objects.isNull(this.request)) {
            final ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
            this.request = client.read(server).compressServerResponse(false).decompressClientRequest(false);
            log.info("Connected clickhouse server by {} protocol, {} port, {} database", server.getProtocol(),
                server.getPort(), server.getDatabase(), server.getDatabase().get());
        }
        return this.request;
    }
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ClickHouseNode node = ClickHouseNode.of("localhost", ClickHouseProtocol.HTTP, 8123, "default");
        final ClickHouseResponse response = ClickHouseClient.newInstance(node.getProtocol()).read(node)
            .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
            .query("SELECT version()").execute().get();
        System.out.println(response.stream().count());
    }
}
