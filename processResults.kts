val bmName = args[0]
while (true) {
    val line = readLine() ?: break

    println(line)

    val (size, result) =
            ".*\\s+(\\d+)\\s+avgt\\s+\\d+\\s+(\\d+\\.\\d+).*"
                    .toRegex().matchEntire(line)?.destructured ?: continue

    println("""##teamcity[buildStatisticValue key='$bmName size=$size' value='$result']""")
}
