val bmName = args[0]
while (true) {
    val line = readLine() ?: break

    println(line)

    val (isIR, size, result) =
            ".*(true|false)\\s+(\\d+)\\s+avgt\\s+\\d+\\s+(\\d+\\.\\d+).*"
                    .toRegex().matchEntire(line)?.destructured ?: continue

    val irPostfix = if (isIR.toBoolean()) " isIR=true" else ""

    println("""##teamcity[buildStatisticValue key='$bmName size=$size${irPostfix}' value='$result']""")
}
