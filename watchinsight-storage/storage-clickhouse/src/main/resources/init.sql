CREATE TABLE IF NOT EXISTS %s (
    Timestamp DateTime64(9) CODEC(Delta, ZSTD(1)),
    TraceId String CODEC(ZSTD(1)),
    SpanId String CODEC(ZSTD(1)),
    ParentSpanId String CODEC(ZSTD(1)),
    TraceState String CODEC(ZSTD(1)),
    SpanName LowCardinality(String) CODEC(ZSTD(1)),
    SpanKind LowCardinality(String) CODEC(ZSTD(1)),
    ServiceName LowCardinality(String) CODEC(ZSTD(1)),
    ResourceAttributes Map(LowCardinality(String), String) CODEC(ZSTD(1)),
    SpanAttributes Map(LowCardinality(String), String) CODEC(ZSTD(1)),
    Duration Int64 CODEC(ZSTD(1)),
    StatusCode LowCardinality(String) CODEC(ZSTD(1)),
    StatusMessage String CODEC(ZSTD(1)),
    Events Nested (
    Timestamp DateTime64(9),
    Name LowCardinality(String),
    Attributes Map(LowCardinality(String), String)
    ) CODEC(ZSTD(1)),
    Links Nested (
                     TraceId String,
                     SpanId String,
                     TraceState String,
                     Attributes Map(LowCardinality(String), String)
    ) CODEC(ZSTD(1)),
    INDEX idx_trace_id TraceId TYPE bloom_filter(0.001) GRANULARITY 1,
    INDEX idx_res_attr_key mapKeys(ResourceAttributes) TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_res_attr_value mapValues(ResourceAttributes) TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_span_attr_key mapKeys(SpanAttributes) TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_span_attr_value mapValues(SpanAttributes) TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_duration Duration TYPE minmax GRANULARITY 1
    ) ENGINE MergeTree()
    %s
    PARTITION BY toDate(Timestamp)
    ORDER BY (ServiceName, SpanName, toUnixTimestamp(Timestamp), TraceId)
    SETTINGS index_granularity=8192, ttl_only_drop_parts = 1;

create table IF NOT EXISTS %s_trace_id_ts (
    TraceId String CODEC(ZSTD(1)),
    Start DateTime64(9) CODEC(Delta, ZSTD(1)),
    End DateTime64(9) CODEC(Delta, ZSTD(1)),
    INDEX idx_trace_id TraceId TYPE bloom_filter(0.01) GRANULARITY 1
    ) ENGINE MergeTree()
    %s
    ORDER BY (TraceId, toUnixTimestamp(Start))
    SETTINGS index_granularity=8192;

CREATE MATERIALIZED VIEW IF NOT EXISTS %s_trace_id_ts_mv
TO %s.%s_trace_id_ts
AS SELECT
              TraceId,
              min(Timestamp) as Start,
              max(Timestamp) as End
   FROM
              %s.%s
   WHERE TraceId!=''
   GROUP BY TraceId;