package io.github.dector.genericsm.models

data class ExerciseMeta(
    val schema: String,
    val id: String,
    val data: ExerciseRoot
)

data class ExerciseRoot(
    val exercise: ExerciseData,
    val tests: TestData
)

data class ExerciseData(
    val fileName: String,
    val content: String
)

data class TestData(
    val name: String,
    val entries: List<TestEntry>
)

data class TestEntry(
    val name: String,
    val content: String
)
