# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/usr/bin/env sh

PRG="$0"
PRGDIR=`dirname "$PRG"`
[ -z "$WATCHINSIGHT_HOME" ] && WATCHINSIGHT_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

WATCHINSIGHT_LOG_DIR="${WATCHINSIGHT_LOG_DIR:-${WATCHINSIGHT_HOME}/logs}"
JAVA_OPTS=" -Xms256M -Xmx512M"

if [ ! -d "${WATCHINSIGHT_LOG_DIR}" ]; then
    mkdir -p "${WATCHINSIGHT_LOG_DIR}"
fi

_RUNJAVA=${JAVA_HOME}/bin/java
[ -z "$JAVA_HOME" ] && _RUNJAVA=java

CLASSPATH="$WATCHINSIGHT_HOME/config:$CLASSPATH"
for i in "$WATCHINSIGHT_HOME"/libs/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

WATCHINSIGHT_OPTIONS=" -Dwatchinsight.logDir=${WATCHINSIGHT_LOG_DIR}"

eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} ${WATCHINSIGHT_OPTIONS} -classpath $CLASSPATH org.watchinsight.starter.WatchInsightStarter \
        2>${WATCHINSIGHT_LOG_DIR}/watchinsight.log 1> /dev/null &"

if [ $? -eq 0 ]; then
    sleep 1
	echo "WatchInsight Server started successfully!"
else
	echo "WatchInsight Server started failure!"
	exit 1
fi
