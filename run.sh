#!/bin/bash

set -e

#export COMPILER_PATH=/home/user/work/kotlin-jps/dist/kotlinc/lib/kotlin-compiler.jar
#export RUNTIME_PATH=/home/user/work/kotlin-jps/dist/kotlinc/lib/kotlin-runtime.jar
#export KOTLINC_PATH=/home/user/work/kotlin-jps/dist/kotlinc/bin/kotlinc
#export JAVA_HOME=/opt/java/jdk1.8.0_144
#export MVN_PATH=mvn

$JAVA_HOME/bin/java -Xmx8024m -Dkotlin.runtime.path=$RUNTIME_PATH \
    -cp $RUNTIME_PATH:$COMPILER_PATH:target/benchmarks.jar \
    org.openjdk.jmh.Main .*$1.*  \
    -wi 30 -i 20 -f 1 -p size="$2"|$KOTLINC_PATH -script processResults.kts $1
