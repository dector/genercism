package io.github.dector.genericsm

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.dector.genericsm.models.ExerciseSpecification
import io.github.dector.genericsm.models.Expected
import java.io.File

class SpecificationsParser {

    private val moshi = Moshi.Builder()
        .add(Expected::class.java, ExpectedValueAdapter())
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

private class ExpectedValueAdapter : JsonAdapter<Expected>() {

    override fun fromJson(reader: JsonReader): Expected? {
        return when (val token = reader.peek()) {
            Token.STRING -> Expected.StringValue(reader.nextString())
            Token.NUMBER -> Expected.IntValue(reader.nextInt())
            Token.BEGIN_OBJECT -> {
                var value: Expected? = null

                reader.beginObject()
                while (reader.peek() != Token.END_OBJECT) {
                    val name = reader.nextName()
                    when (name) {
                        "error" -> value = Expected.Error(reader.nextString())
                    }
                }
                reader.endObject()

                value
            }
            else -> error("Undefined token: $token")
        }
    }

    override fun toJson(writer: JsonWriter, value: Expected?) {
        TODO()
    }
}
