package io.github.dector.genericsm

import java.io.File

fun main() {
    val environment = EnvironmentCfg(
        specificationsDir = File("data/problem-specifications"),
        assetsDir = File("data/assets"),
        outDir = File("generated-exercises")
    )

    execute(environment)
}

private fun execute(environment: EnvironmentCfg) {
    println("Loading exercises...")

    loadExercisesDataFiles(environment)
        .also { println("  found ${it.size} exercises") }
        .asSequence()
        .onEach { println("Processing `${it.parentFile.name}`") }
        .map(environment.parser::parse)
        .forEach { generateExercise(environment, it) }

    println("Done!")
}

data class EnvironmentCfg(
    val specificationsDir: File,
    val assetsDir: File,
    val outDir: File,
    val parser: SpecificationsParser = SpecificationsParser()
)
