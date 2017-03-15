package org.jetbrains.kotlin.benchmarks

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Report(
        val name: String,
        val parameters: String,
        val score: Double,
        val deviation: Double
)

fun parseFile(fileName: String) =
        JSONArray(File(fileName).readText()).map {
            it as JSONObject
            val size = it.getJSONObject("params").getString("size").toInt()
            val score = it.getJSONObject("primaryMetric").getDouble("score")
            val scoreError = it.getJSONObject("primaryMetric").getDouble("scoreError")

            Report(it.getString("benchmark"), size.toString(), score, scoreError)
        }

fun main(args: Array<String>) {
    parseFile("/home/sufix/work/compiler-benchmarks/reports/benchmarks.json").forEach(::println)
}
