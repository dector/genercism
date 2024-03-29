package io.github.dector.genericsm

const val DEBUG_ALL_IN_ONE_PLACE = false

/*
fun generateExercise(environment: EnvironmentCfg, meta: CanonicalData) {
    val exerciseOutDir = if (DEBUG_ALL_IN_ONE_PLACE)
        File(environment.outDir, "__all-together")
    else File(environment.outDir, meta.outDirName())

    generateExerciseFiles(
        assetsDir = environment.assetsDir,
        outDir = exerciseOutDir,
        meta = meta)
}

private fun generateExerciseFiles(assetsDir: File, outDir: File, meta: CanonicalData) {
    outDir.mkdirs()

    writeExerciseSources(outDir, meta)
    writeTestSources(outDir, meta)
    writeBuildGradleSource(outDir)
    copyAssets(assetsDir, outDir)
}

private fun writeExerciseSources(outDir: File, meta: CanonicalData) {
    println("  generating sources...")

    fun generateExerciseFile(): String {
        val functions = meta.cases
            .map { it.property }
            .distinct()

        fun params() = meta.cases
            .flatMap { it.input.entries }
            .distinctBy { it.key }
            .map { it.key to inputType(it.value) }
            .joinToString { (key, type) -> "$key: $type" }

        return functions.joinToString("\n\n") { "fun $it(${params()}): String = TODO(\"Write your solution here\")" }
    }

    meta.sourceFile(outDir)
        .also { it.parentFile.mkdirs() }
        .writeText(generateExerciseFile())
}

private fun writeTestSources(outDir: File, meta: CanonicalData) {
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

        fun tests() = meta.cases
            .withIndex()
            .joinToString("\n\n") { (i, entry) ->
                """
                    |@Test
                    |@Order($i)
                    |fun `${entry.description}`() {
                    |    assertEquals("${entry.expected}", ${entry.functionCallAsString()})
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

private fun CanonicalData.outDirName(): String = exercise

private fun CanonicalData.className(postfix: String = ""): String =
    exercise
        .split("-")
        .joinToString("", postfix = postfix) { it.capitalize() }

private fun CanonicalData.fileName(postfix: String = ""): String =
    this.className(postfix) + ".kt"

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

private fun CanonicalData.sourceFile(outDir: File): File =
    File(outDir, "src/main/kotlin/${fileName()}")

private fun CanonicalData.testSourceFile(outDir: File): File =
    File(outDir, "src/test/kotlin/${fileName("Test")}.kt")
*/
