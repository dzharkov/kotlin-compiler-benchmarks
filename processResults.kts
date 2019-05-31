val bmName = args[0]
while (true) {
    val line = readLine() ?: break

    println(line)

    // Benchmark                                      (isIR)  (size)  (useNI)  Mode  Cnt   Score   Error  Units
    // InferenceFromArgumentCallsBenchmark.benchmark   false       1    false  avgt       14.208
    val (isIR, size, isNI, result) =
            ".*(true|false)\\s+(\\d+)\\s+(true|false)?\\s+avgt\\s+(?:\\d+)?\\s+(\\d+\\.\\d+).*"
                    .toRegex().matchEntire(line)?.destructured ?: continue

    val irPostfix = if (isIR.toBoolean()) " isIR=true" else ""
    val niPostfix = if (isNI.toBoolean() && !isIR.toBoolean()) " isNI=true" else ""

    println("""##teamcity[buildStatisticValue key='$bmName size=$size${irPostfix}$niPostfix' value='$result']""")
}
