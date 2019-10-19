package io.github.dector.genericsm

import io.github.dector.genericsm.models.ExerciseData
import io.github.dector.genericsm.models.ExerciseMeta
import io.github.dector.genericsm.models.TestData
import java.io.File

fun generateExercise(assetsDir: File, outDir: File, meta: ExerciseMeta) {
    println("Generating `${meta.id}`...")

    generateExerciseFiles(
        assetsDir = assetsDir,
        outDir = File(outDir, meta.id),
        meta = meta)
}

private fun generateExerciseFiles(assetsDir: File, outDir: File, meta: ExerciseMeta) {
    outDir.mkdirs()

    writeExerciseSources(outDir, meta)
    writeTestSources(outDir, meta)
    writeBuildGradleSource(outDir)
    copyAssets(assetsDir, outDir)
}

private fun writeExerciseSources(outDir: File, meta: ExerciseMeta) {
    println("  generating sources...")
    fun ExerciseData.file(): File = File(outDir, "src/main/kotlin/$fileName")

    fun generateExerciseFile(exercise: ExerciseData): String = buildString {
        appendln(exercise.content)
        appendln()
    }

    val exercise = meta.data.exercise
    exercise.file()
        .also { it.parentFile.mkdirs() }
        .writeText(generateExerciseFile(exercise))
}

private fun writeTestSources(outDir: File, meta: ExerciseMeta) {
    println("  generating tests...")
    fun TestData.file(): File = File(outDir, "src/test/kotlin/$name.kt")

    fun generateTestFile(tests: TestData): String {
        fun imports() = listOf(
            "org.junit.jupiter.api.Test",
            "org.junit.jupiter.api.Order",
            "org.junit.jupiter.api.TestMethodOrder",
            "org.junit.jupiter.api.MethodOrderer.OrderAnnotation",
            "org.junit.jupiter.api.Assertions.*")
            .sorted()
            .joinToString("\n") { "import $it" }

        fun tests() = tests
            .entries
            .withIndex()
            .joinToString("\n\n") { (i, entry) ->
                """
                    |@Test
                    |@Order($i)
                    |fun ${entry.name}() {
                    |${entry.content.indent()}
                    |}
                """.trimMargin()
            }

        return """
           |${imports()}
           |
           |@TestMethodOrder(OrderAnnotation::class)
           |class ${tests.name} {
           |
           |${tests().indent()}
           |}
           |
        """.trimMargin()
    }

    val tests = meta.data.tests
    tests.file()
        .also { it.parentFile.mkdirs() }
        .writeText(generateTestFile(tests))
}

private fun writeBuildGradleSource(outDir: File) {
    println("  generating build file...")
    val kotlinVersion = "1.3.50"
    val junitVersion = "5.5.1"

    val file = outDir.resolve("build.gradle")
    val content = """
        |plugins {
        |    id 'org.jetbrains.kotlin.jvm' version '$kotlinVersion'
        |}
        |
        |repositories {
        |    jcenter()
        |}
        |
        |dependencies {
        |    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        |
        |    testImplementation 'org.junit.jupiter:junit-jupiter-api:$junitVersion'
        |    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:$junitVersion'
        |}
        |
        |test {
        |    useJUnitPlatform()
        |    testLogging {
        |        exceptionFormat = 'full'
        |        events = ["passed", "failed", "skipped"]
        |    }
        |    failFast = true
        |}
        |
    """.trimMargin()

    file.writeText(content)
}

private fun copyAssets(assetsDir: File, outDir: File) {
    println("  copying assets...")
    val overwrite = assetsDir.isDirectory && outDir.isDirectory
    assetsDir.copyRecursively(outDir, overwrite = overwrite)

    File(outDir, "gradlew").setExecutable(true)
    File(outDir, "gradle.bat").setExecutable(true)
}

private fun String.indent(times: Int = 1): String = this
    .lines()
    .joinToString("\n") {
        if (it.isEmpty())
            it
        else ("    ".repeat(times) + it)
    }
