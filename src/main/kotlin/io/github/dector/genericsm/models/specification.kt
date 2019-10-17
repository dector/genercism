package io.github.dector.genericsm.models

data class ExerciseSpecification(
    val exercise: String,
    val version: String,
    val cases: List<ExerciseCase>
)

data class ExerciseCase(
    val description: String,
    val property: String,
    val input: Map<String, Any?>,
    val expected: String
)
