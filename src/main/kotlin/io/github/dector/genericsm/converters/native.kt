package io.github.dector.genericsm.converters

import io.github.dector.genericsm.CanonicalData
import io.github.dector.genericsm.ExpectedValue
import io.github.dector.genericsm.InputValue
import io.github.dector.genericsm.LabeledTestItem
import io.github.dector.genericsm.LabeledTestItem.LabeledTest
import io.github.dector.genericsm.LabeledTestItem.LabeledTestGroup

fun CanonicalData.asExerciseMeta(): ExerciseMeta {
    val functions: List<FunctionData> = collectFunctions()
    return ExerciseMeta(
        slug = exercise,
        source = asSourceData(functions),
        tests = asTestData(functions)
    )
}

private fun CanonicalData.asSourceData(functions: List<FunctionData>): SourceData {
    return SourceData(
        functions = functions
    )
}

private fun CanonicalData.asTestData(functions: List<FunctionData>): TestData {
    val testCases = flatten(cases)
        .groupBy { it.property }
        .flatMap { entry ->
            entry.value.mapNotNull { test ->
                // FIXME make `map{}`
                if (test.expected is ExpectedValue.Error) return@mapNotNull null

                val name = test.description
                val function = functions.first { it.name == test.property }
                val arguments = test.input.map { it.key to it.value.asValuePair() }.toMap()
                val expectedResult = test.expected.asValuePair()

                TestCase(
                    name = name,
                    function = function,
                    arguments = arguments,
                    expectedResult = expectedResult
                )
            }
        }

    return TestData(
        cases = testCases
    )
}

private fun flatten(data: List<LabeledTestItem>): List<LabeledTest> =
    data.flatMap {
        when (it) {
            is LabeledTest -> listOf(it)
            is LabeledTestGroup -> flatten(it.cases)
        }
    }

private fun CanonicalData.collectFunctions(): List<FunctionData> {
    return flatten(cases)
        .fold(mutableListOf<Triple<String, Type, List<Pair<String, Type>>>>()) { acc, case ->
            val alreadyHasFunction = acc.any { it.first == case.property }

            if (!alreadyHasFunction) {
                val args = case.input.map { (name, value) ->
                    name to Type.from(value)
                }.toList()
                val returnType = Type.from(case.expected)
                acc += Triple(case.property, returnType, args)
            } else {
                //todo
            }

            acc
        }
        .map { (name, returnType, args) ->
            FunctionData(
                name = name,
                returnType = returnType,
                arguments = args.toMap()
            )
        }
        .onEach { println(it) }
}

data class ExerciseMeta(
    val slug: String,
    val source: SourceData,
    val tests: TestData
)

data class SourceData(
    val functions: List<FunctionData>
)

data class TestData(
    val cases: List<TestCase>
)

data class TestCase(
    val name: String,
    val function: FunctionData,
    val arguments: Map<String, Pair<Any, Type>>,
    val expectedResult: Pair<Any, Type>
)

data class FunctionData(
    val name: String,
    val returnType: Type,
    val arguments: Map<String, Type>
)

enum class Type {
    String, Int, Double, Boolean, Array, Map, Null, // TODO

    Unknown;     // fixme

    companion object
}

private fun Type.Companion.from(value: InputValue) = when (value) {
    is InputValue.StringValue -> Type.String
    is InputValue.IntValue -> Type.Int
    is InputValue.DoubleValue -> Type.Double
    is InputValue.BooleanValue -> Type.Boolean
    is InputValue.ArrayValue -> Type.Array
    is InputValue.MapValue -> Type.Map
    is InputValue.NullValue -> Type.Null
    else -> error("Unknown type for input value: ${value::class}")
}

private fun Type.Companion.from(value: ExpectedValue) = when (value) {
    is ExpectedValue.StringValue -> Type.String
    is ExpectedValue.IntValue -> Type.Int
    is ExpectedValue.DoubleValue -> Type.Double
    is ExpectedValue.BooleanValue -> Type.Boolean
    is ExpectedValue.ArrayValue -> Type.Array
    is ExpectedValue.MapValue -> Type.Map
    is ExpectedValue.NullValue -> Type.Null
    is ExpectedValue.Error -> Type.Unknown  // Fixme
    else -> error("Unknown type for expected value: ${value::class}")
}

private fun InputValue.asValuePair(): Pair<Any, Type> = when (this) {
    is InputValue.StringValue -> value to Type.String
    is InputValue.IntValue -> value to Type.Int
    is InputValue.DoubleValue -> value to Type.Double
    is InputValue.BooleanValue -> value to Type.Boolean
    is InputValue.ArrayValue -> values.map { it.asValuePair() } to Type.Array
    is InputValue.MapValue -> value.mapValues { (_, it) -> it.asValuePair() } to Type.Map
    is InputValue.NullValue -> "" to Type.Null
    else -> error("Unknown type for input value: ${this::class}")
}

private fun ExpectedValue.asValuePair(): Pair<Any, Type> = when (this) {
    is ExpectedValue.StringValue -> value to Type.String
    is ExpectedValue.IntValue -> value to Type.Int
    is ExpectedValue.DoubleValue -> value to Type.Double
    is ExpectedValue.BooleanValue -> value to Type.Boolean
    is ExpectedValue.ArrayValue -> values.map { it.asValuePair() } to Type.Array
    is ExpectedValue.MapValue -> value.mapValues { (_, it) -> it?.asValuePair() ?: ("" to Type.Null) } to Type.Map
    is ExpectedValue.NullValue -> "" to Type.Null
    else -> error("Unknown type for expected value: ${this::class}")
}
