package io.github.dector.genericsm.models

data class ExerciseSpecification(
    val exercise: String,
    val version: String,
    val cases: List<ExerciseCase>
)

data class ExerciseCase(
    val description: String,
    val property: String,
    val cases: List<ExerciseCase> = emptyList(),
    val input: Map<String, Any?>,
    val expected: Expected
)

sealed class Expected {
    data class StringValue(val value: String) : Expected()
    data class IntValue(val value: Int) : Expected()
    data class Error(val error: String) : Expected()
}
