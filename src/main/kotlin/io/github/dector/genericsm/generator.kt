package io.github.dector.genericsm

import io.github.dector.genericsm.models.ExerciseMeta
import java.io.File

fun generateExercise(outDir: File, meta: ExerciseMeta) {
    println("Generating sources for `${meta.id}`")

    outDir.mkdirs()

    File(outDir, "__id-${meta.id}").createNewFile()
}
