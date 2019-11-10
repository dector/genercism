package io.github.dector.genercism2

import com.squareup.moshi.Moshi
import io.github.dector.genercism2.Config.ExercisesToProcess.All
import io.github.dector.genercism2.Config.ExercisesToProcess.Only
import java.io.File

fun main() {
    val config = Config(
        specificationsRepo = File("data/v2/specifications"),
        generatedExercisesDir = File("generated-exercises/v2"),
        exercisesToProcess = Only(
            "hello-world"
//            ,"two-fer"
//            ,"hamming"
//            ,"gigasecond"
//            ,"scrabble-score"
//            ,"difference-of-squares"
//            ,"secret-handshake"
//            ,"triangle"
//            ,"saddle-points"
//            ,"flatten-array"
//            ,"word-count"
//            ,"robot-name"
//            ,"rotational-cipher"
//            ,"bank-account"
//            ,"linked-list"
//            ,"binary-search"
        )
    )

    execute(config)
}

private fun execute(config: Config) {
    println("Started")

    // Stage 1: Load specifications
    val specifications = loadSpecifications(config)
    println("Loaded ${specifications.size} specification(s).")

    // Stage 2: Convert specifications to improved format
    val improvedSpecifications = preProcessSpecifications(specifications)

//    generateExercise()
}

data class Config(
    val specificationsRepo: File,
    val generatedExercisesDir: File,
    val exercisesToProcess: ExercisesToProcess
) {

    sealed class ExercisesToProcess {
        object All : ExercisesToProcess()
        data class Only(val slugs: List<String>) : ExercisesToProcess() {
            constructor(vararg slugs: String) : this(slugs.asList())
        }
    }
}

fun loadSpecifications(config: Config): List<ExerciseSpecification> {
    val moshi = Moshi.Builder()
        .build()
    val adapter = moshi.adapter(ExerciseSpecification::class.java)

    val files = config.specificationsRepo
        .resolve("exercises/")
        .listFiles()
        .filter {
            when (val selector = config.exercisesToProcess) {
                All -> true
                is Only -> selector.slugs.contains(it.name)
            }
        }
        .filter { it.isDirectory }
        .map { it.resolve("canonical-data.json") }

    return files
        .map { it.readText() }
        .map(adapter::fromJson)
        .filterNotNull()
}

fun preProcessSpecifications(specifications: List<ExerciseSpecification>): List<ImprovedExerciseSpecification> {
    // TODO
    return emptyList()
}

data class ExerciseSpecification(
    val exercise: String,
    val version: String,
    val cases: List<ExerciseTestCase> = emptyList()
)

data class ExerciseTestCase(
    val description: String,
    val property: String,
    val input: Any, // TODO
    val expected: String
)

data class ImprovedExerciseSpecification(val none: Nothing)
