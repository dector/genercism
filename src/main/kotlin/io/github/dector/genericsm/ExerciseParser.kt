package io.github.dector.genericsm

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.dector.genericsm.models.ExerciseMeta
import java.io.File

class ExerciseParser {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun parseExercise(file: File): ExerciseMeta =
        moshi.adapter(ExerciseMeta::class.java)
            .fromJson(file.readText())
            ?: error("Can't read exercises from: ${file.absolutePath}")

    companion object {

        val Default = ExerciseParser()
    }
}
