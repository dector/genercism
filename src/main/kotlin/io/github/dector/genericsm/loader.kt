package io.github.dector.genericsm

import io.github.dector.genericsm.exercises.`hello-world`
import io.github.dector.genericsm.exercises.`reverse-string`
import io.github.dector.genericsm.models.ExerciseMeta

fun loadExercisesData(): Sequence<ExerciseMeta> =
    exercises.asSequence().map { it.invoke() }

private val exercises = listOf(
    ::`hello-world`,
    ::`reverse-string`
)

/*fun loadExercisesData(exercisesDir: File, parser: ExerciseParser = ExerciseParser.Default): Sequence<ExerciseMeta> =
    exercisesDir.listFiles()!!
        .filter(File::isFile)
        .filter { it.name.endsWith(".json") }
        .asSequence()
        .map(parser::parseExercise)*/
