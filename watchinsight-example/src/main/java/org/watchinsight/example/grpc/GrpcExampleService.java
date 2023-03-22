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

package org.watchinsight.example.grpc;

import com.google.common.primitives.Longs;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc.TraceServiceBlockingStub;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.InstrumentationLibrarySpans;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Span.Event;
import io.opentelemetry.proto.trace.v1.Span.SpanKind;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Created by gerry
 * @date 2023-03-19-23:49
 */
public class GrpcExampleService implements IGprcExampleService {
    
    private ManagedChannel channel;
    
    private String host;
    
    private int port;
    
    public GrpcExampleService(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public Span newSpans() {
        // Create a new span builder
        Span.Builder spanBuilder = Span.newBuilder();
        // Set the span ID and trace ID
        spanBuilder.setSpanId(ByteString.copyFrom(Longs.toByteArray(ThreadLocalRandom.current().nextLong())));
        spanBuilder.setTraceId(ByteString.copyFrom(Longs.toByteArray(ThreadLocalRandom.current().nextLong())));
        // Set the span name
        spanBuilder.setName("example-span");
        // Set the span kind
        spanBuilder.setKind(SpanKind.SPAN_KIND_SERVER);
        // Set the start and end time
        spanBuilder.setStartTimeUnixNano(System.currentTimeMillis() * 1000);
        spanBuilder.setEndTimeUnixNano(System.currentTimeMillis() * 1000);
        // Add attributes to the span
        spanBuilder.addAttributes(
            KeyValue.newBuilder().setKey("example-attribute-key").setValue(AnyValue.newBuilder().build()).build());
        // Add events to the span
        spanBuilder.addEvents(Event.newBuilder()
            .setName("example-event")
            .setTimeUnixNano(System.currentTimeMillis() * 1000)
            .addAttributes(
                KeyValue.newBuilder().setKey("example-event-attribute-key").setValue(AnyValue.newBuilder().build())
                    .build()));
        // Build and return the span
        return spanBuilder.build();
    }
    
    @Override
    public void newChannel() {
        this.channel = ManagedChannelBuilder.forTarget(host + ":" + port).usePlaintext().build();
    }
    
    @Override
    public void export(final Span span) {
        final TraceServiceBlockingStub stub = TraceServiceGrpc.newBlockingStub(channel);
        final ExportTraceServiceRequest request = ExportTraceServiceRequest.newBuilder()
            .addResourceSpans(ResourceSpans.newBuilder()
                .addInstrumentationLibrarySpans(InstrumentationLibrarySpans.newBuilder().addSpans(span))).build();
        stub.export(request);
    }
    
    @Override
    public void stop() {
        channel.shutdown();
    }
    
}
