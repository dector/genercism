package io.github.dector.genericsm

import java.io.File

fun main() {
    val exercisesDir = File("data/exercises")
    val outDir = File("generated-exercises")

    execute(exercisesDir, outDir)
}

private fun execute(exercisesDir: File, outDir: File) {
    if (!outDir.exists()) outDir.mkdirs()

    println("Loading exercises")

    loadExercisesData(exercisesDir)
        .forEach { generateExercise(outDir, it) }

    println("Done")
}
