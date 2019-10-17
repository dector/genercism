package io.github.dector.genericsm.models

data class ExerciseMeta(
    val scheme: Int,
    val id: String,
    val data: ExerciseData
)

data class ExerciseData(
    val exercise: ExerciseFile,
    val tests: TestData
)

data class ExerciseFile(
    val fileName: String,
    val content: String
)

data class TestData(
    val fileName: String,
    val entries: List<TestEntry>
)

data class TestEntry(
    val name: String,
    val content: String
)
