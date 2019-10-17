package io.github.dector.genericsm

import java.io.File

fun main() {
    val exercisesDir = File("data/exercises")
    val outDir = File("generated-exercises")

    execute(exercisesDir, outDir)
}

private fun execute(sourceDir: File, outDir: File) {
    println("Loading exercises")

    loadExercisesData(sourceDir)
        .forEach { generateExercise(outDir, it) }

    println("Done")
}
