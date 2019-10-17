package io.github.dector.genericsm

import java.io.File

fun loadExercisesDataFiles(environment: EnvironmentCfg): List<File> =
    environment.specificationsDir
        .resolve("exercises")
        .listFiles()!!
        .map { it.resolve("canonical-data.json") }
        .filter(File::exists)
