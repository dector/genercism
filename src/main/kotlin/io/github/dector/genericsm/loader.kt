package io.github.dector.genericsm

import io.github.dector.genericsm.models.ExerciseMeta
import java.io.File

fun loadExercisesData(exercisesDir: File, parser: ExerciseParser = ExerciseParser()): Sequence<ExerciseMeta> =
    exercisesDir.listFiles()!!
        .filter(File::isFile)
        .filter { it.name.endsWith(".json") }
        .asSequence()
        .map(parser::parseExercise)
