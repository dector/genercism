package io.github.dector.genericsm

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.dector.genericsm.LabeledTestItem.LabeledTest
import io.github.dector.genericsm.LabeledTestItem.LabeledTestGroup
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
        cases = data.getValue("cases").asLabeledTestItems(),
        comments = data["comments"]?.asComments()
    )
}

data class CanonicalData(
    val version: String,
    val exercise: String,
    val cases: List<LabeledTestItem>,
    val comments: List<String>?
)

sealed class LabeledTestItem {

    object Empty : LabeledTestItem()

    data class LabeledTest(
        val description: String,
        val property: String,
        val input: Any, // fixme
        val expected: Any,  // fixme
        val comments: List<String>?,
        val optional: String?
    ) : LabeledTestItem()

    data class LabeledTestGroup(
        val description: String,
        val cases: List<LabeledTestItem>,
        val comments: List<String>?,
        val optional: String?
    ) : LabeledTestItem()
}

private fun Any.asComments(): List<String> =
    this as List<String>

private fun Any.asLabeledTestItems(): List<LabeledTestItem> {
    return (this as List<Any>)
        .map { it.asLabeledTestItem() }
}

private fun Any.asLabeledTestItem(): LabeledTestItem {
    val data = this as Map<String, Any>

    val hasChildren = data["cases"] != null
    return if (hasChildren) {
        data.asLabeledTestGroup()
    } else {
        data.asLabeledTest()
    }
}

private fun Map<String, Any>.asLabeledTestGroup(): LabeledTestGroup {
    return LabeledTestGroup(
        description = this["description"] as String,
        cases = this.getValue("cases").asLabeledTestItems(),
        optional = this["optional"]?.let { it as String },
        comments = this["comments"]?.asComments()
    )
}

private fun Map<String, Any>.asLabeledTest(): LabeledTest {
    return LabeledTest(
        description = this["description"] as String,
        expected = Unit, // fixme
        input = Unit, // fixme
        optional = this["optional"]?.let { it as String },
        property = this["property"] as String,
        comments = this["comments"]?.asComments()
    )
}
