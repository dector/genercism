package io.github.dector.genericsm

import io.github.dector.genericsm.converters.ExerciseMeta
import io.github.dector.genericsm.converters.FunctionData
import io.github.dector.genericsm.converters.Type
import io.github.dector.genericsm.models.ExerciseCase
import java.io.File

fun generateExercise(environment: EnvironmentCfg, meta: ExerciseMeta) {
    val exerciseOutDir = if (DEBUG_ALL_IN_ONE_PLACE)
        File(environment.outDir, "__all-together")
    else File(environment.outDir, meta.outDirName())

    generateExerciseFiles(
        assetsDir = environment.assetsDir,
        outDir = exerciseOutDir,
        meta = meta)
}

private fun generateExerciseFiles(assetsDir: File, outDir: File, meta: ExerciseMeta) {
    outDir.mkdirs()

    writeExerciseSources(outDir, meta)
    writeTestSources(outDir, meta)
    //writeBuildGradleSource(outDir)       // FIXME
    //copyAssets(assetsDir, outDir)
}

private fun writeExerciseSources(outDir: File, meta: ExerciseMeta) {
    println("  generating sources...")

    fun generateExerciseFile(): String {
        return meta.source
            .functions
            .joinToString("\n\n") {
                val name = it.name
                val args = it.arguments.entries.joinToString { (name, type) -> "$name: $type" }
                val returnType = it.returnType

                "fun $name($args): $returnType = TODO(\"Write your solution here\")"
            }
    }

    meta.sourceFile(outDir)
        .also { it.parentFile.mkdirs() }
        .writeText(generateExerciseFile())
}

private fun writeTestSources(outDir: File, meta: ExerciseMeta) {
    println("  generating tests...")

    fun generateTestFile(): String {
        fun imports() = listOf(
            "org.junit.jupiter.api.Test",
            "org.junit.jupiter.api.Order",
            "org.junit.jupiter.api.TestMethodOrder",
            "org.junit.jupiter.api.MethodOrderer.OrderAnnotation",
            "org.junit.jupiter.api.Assertions.*")
            .sorted()
            .joinToString("\n") { "import $it" }

        fun Pair<Any, Type>.formatValueAsCode(): String = when (second) {
            Type.String -> "\"$first\""
            Type.Int, Type.Double, Type.Boolean -> "$first"
            Type.Array -> "listOf(${(first as List<Pair<Any, Type>>).joinToString { it.formatValueAsCode() }})"
            Type.Map -> "mapOf(${(first as Map<String, Pair<Any, Type>>).entries
                .joinToString { (key, value) -> "$key: " + value.formatValueAsCode() }})"
            Type.Null -> "null"
            else -> error("Undefined type: $second")
        }

        fun FunctionData.formatCallAsCodeWith(args: Map<String, Pair<Any, Type>>): String {
            val argsRow = arguments.entries.joinToString { (name, _) ->
                args.getValue(name).formatValueAsCode()
            }
            return "$name($argsRow)"
        }

        fun tests() = meta.tests
            .cases
            .withIndex()
            .joinToString("\n\n") { (i, case) ->
                val expected = case.expectedResult.formatValueAsCode()
                val functionCall = case.function.formatCallAsCodeWith(case.arguments)

                """
                    |@Test
                    |@Order($i)
                    |fun `${case.name}`() {
                    |    assertEquals($expected, $functionCall)
                    |}
                """.trimMargin()
            }

        return """
           |${imports()}
           | 
           |@TestMethodOrder(OrderAnnotation::class)
           |class ${meta.className()}Test {
           |
           |${tests().indent()}
           |}
        """.trimMargin()
    }

    meta.testSourceFile(outDir)
        .also { it.parentFile.mkdirs() }
        .writeText(generateTestFile())
}

private fun writeBuildGradleSource(outDir: File) {
    println("  generating build file...")
    val kotlinVersion = "1.3.50"
    val junitVersion = "5.5.1"

    val file = outDir.resolve("build.gradle")
    val content = """
        |plugins {
        |  id 'org.jetbrains.kotlin.jvm' version '$kotlinVersion'
        |}
        |
        |repositories {
        |  jcenter()
        |}
        |
        |dependencies {
        |  implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        |
        |  testImplementation 'org.junit.jupiter:junit-jupiter-api:$junitVersion'
        |  testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:$junitVersion'
        |}
        |
        |test {
        |  useJUnitPlatform()
        |  testLogging {
        |    exceptionFormat = 'full'
        |    events = ["passed", "failed", "skipped"]
        |  }
        |  failFast = true
        |}
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

private fun ExerciseMeta.outDirName(): String = slug

private fun ExerciseMeta.className(postfix: String = ""): String =
    slug
        .split("-")
        .joinToString("", postfix = postfix) { it.capitalize() }

private fun ExerciseMeta.fileName(postfix: String = ""): String =
    this.className(postfix) + ".kt"

private fun ExerciseMeta.sourceFile(outDir: File): File =
    File(outDir, "src/main/kotlin/${fileName()}")

private fun ExerciseMeta.testSourceFile(outDir: File): File =
    File(outDir, "src/test/kotlin/${fileName("Test")}.kt")

private fun ExerciseCase.functionCallAsString(): String = run {
    val name = property

    fun args(): String =
        input.values.joinToString { "\"$it\"" }

    "$name(${args()})"
}

private fun inputType(value: Any?): String = when (value) {
    is String -> "String"
    is Int -> "Int"
    is Double -> "Double"
    else -> error("Undefined type of value: `$value`.")
}
