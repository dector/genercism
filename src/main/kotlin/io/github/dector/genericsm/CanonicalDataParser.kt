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

    data class LabeledTest(
        val description: String,
        val property: String,
        val input: Map<String, InputValue>,
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

sealed class InputValue {

    object NullValue : InputValue()
    data class StringValue(val value: String) : InputValue()
    data class BooleanValue(val value: Boolean) : InputValue()
    data class IntValue(val value: Int) : InputValue()
    data class DoubleValue(val value: Double) : InputValue()
    data class ArrayValue(val values: List<InputValue>) : InputValue()
    data class MapValue(val value: Map<String, InputValue>) : InputValue()
}

sealed class ExpectedValue {
    object NullValue : ExpectedValue()
    data class StringValue(val value: String) : ExpectedValue()
    data class BooleanValue(val value: Boolean) : ExpectedValue()
    data class IntValue(val value: Int) : ExpectedValue()
    data class DoubleValue(val value: Double) : ExpectedValue()
    data class ArrayValue(val values: List<ExpectedValue>) : ExpectedValue()
    data class MapValue(val value: Map<String, ExpectedValue?>) : ExpectedValue()

    data class Error(val message: String) : ExpectedValue()
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
        expected = getValue("expected").asExpectedValue(),
        input = getValue("input").asInputObject(),
        optional = this["optional"]?.let { it as String },
        property = this["property"] as String,
        comments = this["comments"]?.asComments()
    )
}

private fun Any.asInputObject(): Map<String, InputValue> {
    fun parseValue(value: Any?): InputValue = when (value) {
        is String ->
            InputValue.StringValue(value)
        is Boolean ->
            InputValue.BooleanValue(value)
        is Int ->
            InputValue.IntValue(value)
        is Double ->
            InputValue.DoubleValue(value)
        is List<*> ->
            InputValue.ArrayValue(
                value.map { parseValue(it) })
        is Map<*, *> ->
            InputValue.MapValue(value.asInputObject())
        null ->
            InputValue.NullValue
        else ->
            error("Unknown type: ${value::class}")
    }

    return (this as Map<String, Any?>)
        .map { (key, value) -> key to parseValue(value) }
        .toMap()
}

private fun Any.asExpectedValue(): ExpectedValue {
    fun parseValue(value: Any?): ExpectedValue = when (value) {
        is String ->
            ExpectedValue.StringValue(value)
        is Boolean ->
            ExpectedValue.BooleanValue(value)
        is Int ->
            ExpectedValue.IntValue(value)
        is Double ->
            ExpectedValue.DoubleValue(value)
        is List<*> ->
            ExpectedValue.ArrayValue(
                value.map { parseValue(it) })
        is Map<*, *> -> if (value["error"] != null) {
            val data = value as Map<String, Any>
            ExpectedValue.Error(data.getValue("error") as String)
        } else {
            ExpectedValue.MapValue(value.asExpectedValueMap())
        }
        null ->
            ExpectedValue.NullValue
        else ->
            error("Unknown type: ${value::class}")
    }

    return parseValue(this)
}

private fun Map<*, *>.asExpectedValueMap(): Map<String, ExpectedValue?> {
    return (this as Map<String, Any?>)
        .map { (key, value) -> key to value?.asExpectedValue() }
        .toMap()
}
