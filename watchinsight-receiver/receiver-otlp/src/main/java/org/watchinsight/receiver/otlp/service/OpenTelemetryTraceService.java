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

package org.watchinsight.receiver.otlp.service;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc.TraceServiceImplBase;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Created by gerry
 * @date 2023-03-21-00:05
 */
@Slf4j
public class OpenTelemetryTraceService extends TraceServiceImplBase implements IOpentelemetryService {
    
    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        log.info(request.toString());
        for (ResourceSpans spans : request.getResourceSpansList()) {
        
        }
        responseObserver.onNext(ExportTraceServiceResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
