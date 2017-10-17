#!/bin/bash

#export COMPILER_PATH=/home/sufix/work/kotlin-gkb/dist/kotlinc/lib/kotlin-compiler.jar
#export RUNTIME_PATH=/home/sufix/work/kotlin-gkb/dist/kotlinc/lib/kotlin-runtime.jar
#export KOTLINC_PATH=/home/sufix/work/kotlin-gkb/dist/kotlinc/bin/kotlinc
#export JAVA_HOME=/opt/java/jdk1.8.0_102
#export MVN_PATH=mvn

$MVN_PATH clean install -Dkotlin.compiler.path=$COMPILER_PATH

$JAVA_HOME/bin/java -Xmx8024m -Dkotlin.runtime.path=$RUNTIME_PATH \
    -cp $RUNTIME_PATH:$COMPILER_PATH:target/benchmarks.jar \
    org.openjdk.jmh.Main .*$1.*  \
    -wi 20 -i 20 -f 1 -p size="$2"|$KOTLINC_PATH -script processResults.kts
