package io.github.dector.genericsm.models

import com.autodsl.annotation.AutoDsl

@AutoDsl(dslName = "meta")
data class ExerciseMeta(
    val schema: String,
    val id: String,
    val data: ExerciseRoot
)

@AutoDsl(dslName = "data")
data class ExerciseRoot(
    val exercise: ExerciseData,
    val tests: TestData
)

@AutoDsl(dslName = "exercise")
data class ExerciseData(
    val fileName: String,
    val content: String
)

@AutoDsl(dslName = "tests")
data class TestData(
    val name: String,
    val entries: List<TestEntry>
)

@AutoDsl(dslName = "entry")
data class TestEntry(
    val name: String,
    val content: String
)
