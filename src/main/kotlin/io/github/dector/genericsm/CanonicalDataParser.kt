package io.github.dector.genericsm

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File

class CanonicalDataParser {

    private val moshi = Moshi.Builder()
        .build()

    fun parse(file: File): CanonicalData {
        val data = moshi.adapter<Map<String, Any>>(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
            .fromJson(file.readText())
            ?: error("Failed to parse file `${file.absolutePath}`")

        return buildCanonicalData(data)
    }
}

private fun buildCanonicalData(data: Map<String, Any>): CanonicalData {
    return CanonicalData(
        version = data["version"] as String,
        exercise = data["exercise"] as String,
        cases = Unit, // fixme
        comments = data["comments"]?.asComments()
    )
}

data class CanonicalData(
    val version: String,
    val exercise: String,
    val cases: Unit,
    val comments: List<String>?
)

private fun Any.asComments(): List<String> {
    val list = this as List<String>

    return list
}
