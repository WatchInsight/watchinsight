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
    url: 1
    port: 19100
  grpc:
    host: '0.0.0.0'
    port: 19090
    token: '123456'
    workThreads: 100
  kafka:
    url: 3

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
  choices: [ clickhouse, elasticsearch ]
  clickhouse:
    url: 'tcp://127.0.0.1:9000?dial_timeout=10s&compress=lz4'
    database: 'otel'
  elasticsearch:
    url: 'http://127.0.0.1:8080'
    index: 'otel'
```

1. **`core`** or **`receiver`** or **`storage`** is the module.
1. **`choices`** Choice one or many out of the all providers listed provider, the unselected ones take no effect as if they were deleted.

List the required modules here
1. **core**. All module providers & services basic.
1. **receiver**. Provide otlp receiver data & dispatch, processor.
1. **storage**. Make the observability's data or metadata process result persistence.

## storage dependency
### clickhouse

+ Install clickhouse for macos docker
    + Step1: Download [docker](https://desktop.docker.com/mac/main/arm64/Docker.dmg?utm_source=docker&utm_medium=webreferral&utm_campaign=dd-smartbutton&utm_location=module) and install 
    + Step2: Download [docker hub](https://hub.docker.com/r/clickhouse/clickhouse-server)
    or CMD
      ```shell
      docker pull clickhouse/clickhouse-server
      ```
    + Step3: CMD
      ```shell
      #8123 is Http Protocol Port
      #9000 is Tcp Protocol Port
      #9004 is Mysql Protocol Port
      #9005 is Postgresql Protocol Port
      #9100 is Grpc Protocol Port``
      docker run -d -p 8123:8123 -p9000:9000 -p9100:9100 --name clickhouse-server --ulimit nofile=262144:262144 clickhouse/clickhouse-server
      ```
+ IDEA add database, create schema ``watchinsight``
    ```shell
    url: localhost
    port: 8123
    user: default
    password: 
    database: watchinsight
    driver: jdbc:clickhouse://localhost:8123
    ```
