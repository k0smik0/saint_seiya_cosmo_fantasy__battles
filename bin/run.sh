#!/bin/bash

DOWNLOAD=$1
#LOAD="-l"

ARGS="credentials__sscfse_battles_collector.json $@"
MAIN="net.iubris.sscfse.battles_collector.Main"
EXEC_OPTIONS="-Dexec.cleanupDaemonThreads=false -Dexec.killAfter=-1"
mvn exec:java -Dexec.mainClass=$MAIN $EXEC_OPTIONS -Dexec.args="$ARGS"