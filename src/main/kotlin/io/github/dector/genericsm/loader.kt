package io.github.dector.genericsm

import java.io.File

private val LOAD_ONLY_FROM = listOf<String>(
//    "pov"
)

fun loadExercisesDataFiles(environment: EnvironmentCfg): List<File> =
    environment.specificationsDir
        .resolve("exercises")
        .listFiles()!!
        .filter { include(it) }
        .map { it.resolve("canonical-data.json") }
        .filter(File::exists)

private fun include(file: File): Boolean =
    if (LOAD_ONLY_FROM.isEmpty())
        true
    else LOAD_ONLY_FROM.contains(file.name)
