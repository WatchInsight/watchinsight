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

package org.watchinsight.core.storage;

import org.watchinsight.core.model.StorageData;
import org.watchinsight.core.service.ServiceDefine;

/**
 * Provider operator for table
 *
 * @author Created by gerry
 * @date 2023-03-30-01:02
 */
public interface ITableService extends ServiceDefine {
    
    /**
     * By config init tables for db
     *
     * @param tableName
     * @param ttlDays
     * @param sql
     * @throws Exception
     */
    void createTable(final String tableName, int ttlDays, final String sql) throws Exception;
    
    /**
     * insert data to db
     *
     * @param storageData
     * @throws Exception
     */
    <T extends StorageData> void insertTable(final T storageData) throws Exception;
    
    /**
     * table prefix
     *
     * @return
     */
    String keyPrefix();
    
}
