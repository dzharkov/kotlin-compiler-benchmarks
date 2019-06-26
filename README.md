# kotlin-compiler-benchmarks

## Preparing
 - clone Kotlin project
 - run `./gradlew dist` in Kotlin project
 - write-out path to Kotlin dist in `pom.xml` at `<kotlin.compiler.path>/home/sufix/work/kotlin-gkb/dist/kotlinc/lib/kotlin-compiler.jar</kotlin.compiler.path>`
 - run `mvn clean install`
 - edit env setup in `run.sh`
   * `export COMPILER_PATH=path-to-kotlin/dist/kotlinc/lib/kotlin-compiler.jar`
   * `export RUNTIME_PATH=path-to-kotlin/dist/kotlinc/lib/kotlin-runtime.jar` or `kotlin-stdlib.jar` for more load
   * `export KOTLINC_PATH=path-to-kotlin/dist/kotlinc/bin/kotlinc`
 - execute `./run.sh`, parameters is `./run.sh [BenchmarkRegex] [Size]`, ex: `./run.sh Inference 5000`
