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

package org.watchinsight.receiver.otlp;

import org.watchinsight.core.CoreGprcProvider;
import org.watchinsight.core.CoreModule;
import org.watchinsight.core.provider.ProviderDefine;
import org.watchinsight.core.service.IServerService;
import org.watchinsight.receiver.otlp.service.OpenTelemetryTraceService;
import org.watchinsight.receiver.otlp.service.OpentelemetryMetricService;

/**
 * @author Created by gerry
 * @date 2023-03-13-23:44
 */
public class ReceiverOtlpProvider extends ProviderDefine {
    
    public static final String OTLP = "otlp";
    
    private OtlpProviderConfig config;
    
    public ReceiverOtlpProvider() {
        this.config = new OtlpProviderConfig();
    }
    
    @Override
    public String name() {
        return OTLP;
    }
    
    @Override
    public OtlpProviderConfig createConfig() {
        return this.config;
    }
    
    @Override
    public void prepare() {
        super.register(OpenTelemetryTraceService.class, new OpenTelemetryTraceService());
        super.register(OpentelemetryMetricService.class, new OpentelemetryMetricService());
    }
    
    @Override
    public void start() {
    
    }
    
    @Override
    public void after() {
        final OpenTelemetryTraceService traceService = super.getService(OpenTelemetryTraceService.class);
        final OpentelemetryMetricService metricService = super.getService(OpentelemetryMetricService.class);
        final IServerService serverService = super.find(CoreModule.CORE, CoreGprcProvider.GRPC)
            .getService(IServerService.class);
        serverService.addService(metricService).addService(traceService);
    }
    
    @Override
    public void stop() {
    }
}
