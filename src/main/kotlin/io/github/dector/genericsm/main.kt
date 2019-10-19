package io.github.dector.genericsm

import java.io.File

fun main() {
    val exercisesDir = File("data/exercises")
    val assetsDir = File("data/assets")
    val outDir = File("generated-exercises")

    execute(assetsDir, exercisesDir, outDir)
}

private fun execute(assetsDir: File, sourceDir: File, outDir: File) {
    println("Loading exercises...")

    //loadExercisesData(sourceDir)
    loadExercisesData()
        .forEach { generateExercise(assetsDir, outDir, it) }

    println("Done!")
}
