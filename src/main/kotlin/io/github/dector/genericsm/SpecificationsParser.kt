package io.github.dector.genericsm

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.dector.genericsm.models.ExerciseSpecification
import java.io.File

class SpecificationsParser {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
//        .add(PolymorphicJsonAdapterFactory.of(Input::class.java, "__type")
//            .withSubtype(Input.Empty::class.java, Input.Empty::class.java.simpleName)
//            .withSubtype(Input.Valued::class.java, Input.Valued::class.java.simpleName)
//        )
        .build()

    fun parse(file: File): ExerciseSpecification {
        val adapter = moshi.adapter(ExerciseSpecification::class.java)
        val data = adapter.fromJson(file.readText())
            ?: error("Can't read exercises from: ${file.absolutePath}")

        return data
    }
}
