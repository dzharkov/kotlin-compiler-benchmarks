while (true) {
    val line = readLine() ?: break
    val (size, result) =
            ".*\\s+(\\d+)\\s+avgt\\s+\\d+\\s+(\\d+\\.\\d+).*"
                    .toRegex().matchEntire(line)?.destructured ?: continue

    println("""##teamcity[buildStatisticValue key='size=$size' value='$result']""")
}
