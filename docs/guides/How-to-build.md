# How to build project
This document helps people compile and build projects in your Maven and set up your IDE.

### Build from GitHub
1. Prepare git, JDK8 and maven3
1. Clone project
    ```bash
    git clone https://github.com/WatchInsight/watchinsight.git
    cd giot/
    ```
1. Run `./mvn clean package -DskipTests`
1. Packages are in `/dist` (.tar.gz for Linux or MacOS).
1. Execute file for Linux or MacOS or double click `/dist/watchinsight-all-bin/bin/watchinsight-start.bat` for windows
    ```bash
    unzip /dist/watchinsight-all-bin.tar.gz
    sh /dist/watchinsight-all-bin/bin/watchinsight-start.sh
    ```