WatchInsight
==========
**WatchInsight** is an observability platform built on Metrics, Tracing, Logging, Alert, Events, and Profiling, providing microservice monitoring, cloud-native infrastructure monitoring, business monitoring, troubleshooting tools, and continuous analysis

# Documentation
# Quick Start
Quick starter `Module` -> `watchinsight-starter` by source code.
```java
public class WatchInsightStarter {
    
    public static void main(String[] args) throws FileNotFoundException {
        //Build configuration loader
        final ConfigLoader<ApplicationConfiguration> configLoader = new ApplicationConfigLoader();
        //Load yml to application configuration
        final ApplicationConfiguration configuration = configLoader.load("application.yml");
        //Build module manager to init module & providers
        final DefaultModuleManager manager = new DefaultModuleManager(configuration);
        manager.init();
    }

}
```
# How to build
Follow this [document](docs/guides/How-to-build.md)
# Downloads
# Live Demo
# License
[Apache 2.0 License.](LICENSE.txt)
