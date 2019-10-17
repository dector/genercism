package io.github.dector.genericsm

import java.io.File

fun main() {
    val environment = EnvironmentCfg(
        specificationsDir = File("data/problem-specifications"),
        assetsDir = File("data/assets"),
        outDir = File("generated-exercises"),
        skipFailed = true,
        onlyParse = true
    )

    execute(environment)
}

private fun execute(environment: EnvironmentCfg) {
    println("Loading exercises...")

    val counters = object {
        val succeeded = mutableListOf<String>()
        val failed = mutableListOf<String>()
    }

    loadExercisesDataFiles(environment)
        .also { println("  found ${it.size} exercises") }
        .asSequence()
        .onEach { println("Processing `${it.parentFile.name}`") }
        .mapNotNull { file ->
            val parseResult = runCatching { environment.parser.parse(file) }
                .onFailure { counters.failed += file.parentFile.name }
                .onSuccess { counters.succeeded += file.parentFile.name }

            val result = if (environment.skipFailed) {
                parseResult
                    .onFailure {
                        it.printStackTrace()
                        println("Skipping `${file.parentFile.name}`")
                    }
                    .getOrNull()
            } else parseResult.getOrThrow()

            result.takeUnless { environment.onlyParse }
        }
        .forEach { meta ->
            generateExercise(environment, meta)
        }

    println()
    println("Succeeded: ${counters.succeeded.size}")
    counters.succeeded
        .takeIf { it.isNotEmpty() }
        ?.let(::println)

    println("Failed: ${counters.failed.size}")
    counters.failed
        .takeIf { it.isNotEmpty() }
        ?.let(::println)

    println("Done!")
}

data class EnvironmentCfg(
    val specificationsDir: File,
    val assetsDir: File,
    val outDir: File,
    val parser: SpecificationsParser = SpecificationsParser(),
    val skipFailed: Boolean = false,
    val onlyParse: Boolean = false
)
