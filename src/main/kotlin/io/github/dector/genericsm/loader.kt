package io.github.dector.genericsm

import java.io.File

private val DEV_EXERCISES = listOf(
    "hello-world",
    "reverse-string"
)

fun loadExercisesDataFiles(environment: EnvironmentCfg): List<File> =
    environment.specificationsDir
        .resolve("exercises")
        .listFiles()!!
        .filter { DEV_EXERCISES.contains(it.name) }
        .map { it.resolve("canonical-data.json") }
        .filter(File::exists)
