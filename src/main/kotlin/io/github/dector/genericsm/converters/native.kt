package io.github.dector.genericsm.converters

import io.github.dector.genericsm.CanonicalData
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
    fun findFunctions(): List<FunctionData> {
        return emptyList()//fixme
    }

    return SourceData(
        functions = findFunctions()
    )
}

private fun CanonicalData.asTestData(functions: List<FunctionData>): TestData {
    return TestData(Unit)
}

private fun CanonicalData.collectFunctions(): List<FunctionData> {
    fun flatten(data: List<LabeledTestItem>): List<LabeledTest> =
        data.flatMap {
            when (it) {
                is LabeledTest -> listOf(it)
                is LabeledTestGroup -> flatten(it.cases)
            }
        }

    return flatten(cases)
        .fold(mutableMapOf<String, MutableList<Pair<String, Type>>>()) { acc, case ->
            if (!acc.containsKey(case.property)) {
                acc += case.property to mutableListOf()
            } else {
                //todo
            }

            acc
        }
//        .distinctBy { it.property }
//        .onEach { println(it) }
        .map { (name, args) ->
            FunctionData(
                name = name,
                returnType = Type.Unknown,
                arguments = emptyMap()  // fixme
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

data class TestData(val empty: Unit)

data class FunctionData(
    val name: String,
    val returnType: Type,
    val arguments: Map<String, Type>
)

enum class Type {
    String, Int, Double, // TODO

    Unknown     // fixme
}
