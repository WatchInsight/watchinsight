# Server setup
WatchInsight backend startup behaviours are driven by `config/application.yml`.
Understood the setting file will help you to read this document.

## Startup script
The default startup scripts are `/shell/bin/watchinsight-start.sh`(.bat). 

## application.yml
The core concept behind this setting file is, WatchInsight server is based on pure module & provider & service design. 
User can which or assemble the provider features by their own requirements.

Example:
```yaml
core:
  choices: [ default, http, grpc, kafka ]
  default:
    core: 0
  ##provider transports for http&grpc&kafka
  http:
    url: http://127.0.0.1
  grpc:
    host: 0.0.0.0
    port: 19090
    workThreads: 100
  kafka:
    url: broker.watchinsight.com


receiver:
  ##data format support otlp&meter
  choices: [ otlp, meter ]
  otlp:
    metrics: dddd
    trace: dd
    logs: ddd
  meter:
    metrics: cc
    trace: ccc
    logs: c

storage:
  choices: [ clickhouse ]
  clickhouse:
  elasticsearch:
```

1. **`core`** or **`receiver`** or **`storage`** is the module.
1. **`choices`** Choice one or many out of the all providers listed provider, the unselected ones take no effect as if they were deleted.

List the required modules here
1. **core**. All module providers & services basic.
1. **receiver**. Provide otlp receiver data & dispatch, processor.
1. **storage**. Make the observability's data or metadata process result persistence.