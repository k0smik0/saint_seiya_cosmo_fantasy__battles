#!/bin/bash

DOWNLOAD=$1
#LOAD="-l"

ARGS="project-id-0637669633636693062-df0a507491f9.web.json project-id-0637669633636693062-df0a507491f9.service_account.json $@" 
MAIN="net.iubris.sscfse.battles_collector.Main"
EXEC_OPTIONS="-Dexec.cleanupDaemonThreads=false -Dexec.killAfter=-1 -Djava.net.preferIPv4Stack=true"
mvn exec:java -Dexec.mainClass=$MAIN $EXEC_OPTIONS -Dexec.args="$ARGS" 
