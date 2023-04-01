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

import java.util.List;
import lombok.Data;
import org.watchinsight.core.provider.ProviderConfig;

/**
 * @author Created by gerry
 * @date 2023-03-23-23:45
 */
@Data
public class ClickHouseConfig implements ProviderConfig {
    
    private String url;
    
    private int port;
    
    private String user;
    
    private String password;
    
    private String database = "watchinsight";
    
    /**
     * Since the arrays in the yml configuration file are loaded as ArrayLists by the YAML, all array properties inside
     * ProviderConfig should be of the ArrayList type.
     */
    private List<String> tables;
    
    private int ttlDays = 3;
    
}
