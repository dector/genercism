package io.github.dector.genericsm

import io.github.dector.genericsm.models.ExerciseData
import io.github.dector.genericsm.models.ExerciseMeta
import io.github.dector.genericsm.models.TestData
import java.io.File

fun generateExercise(outDir: File, meta: ExerciseMeta) {
    println("Generating sources for `${meta.id}`...")

    generateExerciseFiles(
        outDir = File(outDir, meta.id),
        meta = meta)
}

private fun generateExerciseFiles(outDir: File, meta: ExerciseMeta) {
    outDir.mkdirs()

    writeExerciseSources(outDir, meta)
    writeTestSources(outDir, meta)
}

private fun writeExerciseSources(outDir: File, meta: ExerciseMeta) {
    fun ExerciseData.file(): File = File(outDir, "src/main/kotlin/$fileName")

    fun generateExerciseFile(exercise: ExerciseData): String = buildString {
        appendln(exercise.content)
    }

    val exercise = meta.data.exercise
    exercise.file()
        .also { it.parentFile.mkdirs() }
        .writeText(generateExerciseFile(exercise))
}

private fun writeTestSources(outDir: File, meta: ExerciseMeta) {
    fun TestData.file(): File = File(outDir, "test/main/kotlin/$name.kt")

    fun generateTestFile(tests: TestData): String {
        fun imports() = listOf("org.junit.Test", "kotlin.test.*")
            .joinToString("\n") { "import $it" }

        fun tests() = tests
            .entries
            .joinToString("\n\n") { entry ->
                """
                    |@Test
                    |fun ${entry.name}() {
                    |${entry.content.indent()}
                    |}
                """.trimMargin()
            }

        return """
           |${imports()}
           | 
           |class ${tests.name} {
           |
           |${tests().indent()}
           |}
        """.trimMargin()
    }

    val tests = meta.data.tests
    tests.file()
        .also { it.parentFile.mkdirs() }
        .writeText(generateTestFile(tests))
}

private fun String.indent(times: Int = 1): String = this
    .lines()
    .joinToString("\n") {
        if (it.isEmpty())
            it
        else ("    ".repeat(times) + it)
    }
