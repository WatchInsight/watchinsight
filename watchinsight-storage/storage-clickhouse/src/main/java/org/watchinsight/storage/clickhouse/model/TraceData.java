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

package org.watchinsight.storage.clickhouse.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.watchinsight.core.model.StorageData;

/**
 * @author Created by gerry
 * @date 2023-04-09-22:38
 */
@Data
public class TraceData implements StorageData {
    
    private LocalDateTime Timestamp;
    
    private String TraceId;
    
    private String SpanId;
    
    private String ParentSpanId;
    
    private String TraceState;
    
    private String SpanName;
    
    private String SpanKind;
    
    private String ServiceName;
    
    private Map<String, String> ResourceAttributes;
    
    private Map<String, String> SpanAttributes;
    
    private Long Duration;
    
    private String StatusCode;
    
    private String StatusMessage;
    
    private List<Event> Events;
    
    private List<Link> Links;
    
    @Data
    public static class Event {
        
        private LocalDateTime Timestamp;
        
        private String Name;
        
        private Map<String, String> Attributes;
    }
    
    @Data
    public static class Link {
        
        private String TraceId;
        
        private String SpanId;
        
        private String TraceState;
        
        private Map<String, String> Attributes;
        
    }
}