package io.github.dector.genercism2

import io.github.dector.genercism2.FunctionCall.AType
import java.io.File

fun writeSources(rootDir: File, spec: ImprovedExerciseSpecification) {
    rootDir.resolve("src/main/kotlin/${spec.exerciseClassName}.kt")
        .also { it.parentFile.mkdirs() }
        .writeText(generateSourceFileContent(spec))
}

fun generateSourceFileContent(spec: ImprovedExerciseSpecification): String {
    fun functions() = spec
        .exercise
        .functions
        .joinToString("\n\n") { func ->
            "fun ${func.stringifySignature()} = TODO()"
        }

    fun fileContent() = run {
        val className = spec.exerciseClassName
        """
            |class ${className} {
            |
            |${functions().indent()}
            |}
        """.trimMargin()
    }

    return fileContent()
}

fun writeTestSources(rootDir: File, spec: ImprovedExerciseSpecification) {
    rootDir.resolve("src/test/kotlin/${spec.testClassName}.kt")
        .also { it.parentFile.mkdirs() }
        .writeText(generateTestFileContent(spec))
}

fun generateTestFileContent(spec: ImprovedExerciseSpecification): String {
    fun imports() = listOf(
        "org.junit.jupiter.api.Test",
        "org.junit.jupiter.api.Order",
        "org.junit.jupiter.api.TestMethodOrder",
        "org.junit.jupiter.api.MethodOrderer.OrderAnnotation",
        "org.junit.jupiter.api.Assertions.*")
        .sorted()
        .joinToString("\n") { "import $it" }

    fun tests() = spec
        .testCases
        .withIndex()
        .joinToString("\n\n") { (i, entry) ->
            """
                    |@Test
                    |@Order($i)
                    |fun `${entry.name}`() {
                    |${entry.content().indent()}
                    |}
                """.trimMargin()
        }

    return """
           |${imports()}
           |
           |@TestMethodOrder(OrderAnnotation::class)
           |class ${spec.testClassName} {
           |
           |${tests().indent()}
           |}
           |
        """.trimMargin()
}

fun writeBuildGradleSource(outDir: File) {
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

fun copyAssets(assetsDir: File, outDir: File) {
    println("  copying assets...")
    val overwrite = assetsDir.isDirectory && outDir.isDirectory
    assetsDir.copyRecursively(outDir, overwrite = overwrite)

    File(outDir, "gradlew").setExecutable(true)
    File(outDir, "gradle.bat").setExecutable(true)
}

fun String.indent(times: Int = 1, indent: String = "    "): String = this
    .lines()
    .joinToString("\n") {
        if (it.isEmpty())
            it
        else (indent.repeat(times) + it)
    }

fun ImprovedTestCase.content() = when (call.result) {
    is TestCall.AResult.Success -> {
        val expected = call.result.value.stringify()
        """
            val result = ${stringifyCall()}
            assertEquals($expected, result)
        """.trimIndent()
    }
    is TestCall.AResult.Exception -> {
        """
            TODO()
        """.trimIndent()
    }
}

fun ImprovedTestCase.stringifyCall() = buildString {
    append(call.functionName)

    append("(")
    when {
        call.arguments.isEmpty() -> {
            // Do nothing
        }
        else -> TODO()
    }
    append(")")
}

fun FunctionCall.stringifySignature() = buildString {
    append(functionName)

    append("(")
    arguments.entries.joinToString { (name, type) ->
        "$name: ${type.stringify()}"
    }.let { append(it) }
    append(")")

    if (resultType != AType.AUnit) {
        append(": ")
        append(resultType.stringify())
    }
}

fun AType.stringify(): String = when (this) {
    AType.AString -> "String"
    AType.AUnit -> "Unit"
    else -> TODO()
}
