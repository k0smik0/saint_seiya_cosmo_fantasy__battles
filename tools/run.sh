#!/bin/bash 
 
DOWNLOAD=$1 
#LOAD="-l" 
  
ARGS="credentials__sscfse_battles_collector.json $@" 
MAIN="net.iubris.sscfse.battles_collector.Main" 
EXEC_OPTIONS="-Dexec.cleanupDaemonThreads=false -Dexec.killAfter=-1 -Djava.net.preferIPv6Addresses=true" 
mvn -e exec:java -Dexec.mainClass=$MAIN $EXEC_OPTIONS -Dexec.args="$ARGS" 
