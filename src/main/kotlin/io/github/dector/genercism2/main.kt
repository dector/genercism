package io.github.dector.genercism2

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import io.github.dector.genercism2.Config.ExercisesToProcess.All
import io.github.dector.genercism2.Config.ExercisesToProcess.Only
import io.github.dector.genercism2.FunctionCall.AType
import io.github.dector.genercism2.FunctionCall.AType.AUnit
import io.github.dector.genercism2.TestCall.AResult
import io.github.dector.genercism2.TestCall.AValue
import io.github.dector.genercism2.TestCall.AValue.AString
import java.io.File

fun main() {
    val config = Config(
        specificationsRepo = File("data/v2/specifications"),
        assetsDir = File("data/v2/assets"),
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
    println("Converting...")
    val improvedSpecifications = preProcessSpecifications(specifications)

    // Stage 3: Generate exercises
    println("Generating...")
    generateExercises(config, improvedSpecifications)
}

data class Config(
    val specificationsRepo: File,
    val assetsDir: File,
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
    val moshi = moshi()
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

    fun convertTestCase(case: ExerciseTestCase): ImprovedTestCase {
        return ImprovedTestCase(
            name = case.description.simplifyForName(),
            description = case.description,
            call = case.asTestCall()
        )
    }

    fun convertSpecification(spec: ExerciseSpecification): ImprovedExerciseSpecification {
        fun exercise(spec: ExerciseSpecification): ImprovedExercise {
            fun args(case: ExerciseTestCase) = case
                .input
                .map { (name, value) ->
                    name to when (value) {
                        is String -> AType.AString
                        else -> TODO()
                    }
                }
                .toMap()

            val funcs = spec.cases.map { case ->
                FunctionCall(
                    functionName = case.property,
                    arguments = args(case),
                    resultType = when (case.expected) {
                        is String -> AType.AString
                        else -> TODO()
                    }
                )
            }

            return ImprovedExercise(
                functions = funcs
            )
        }

        return ImprovedExerciseSpecification(
            slug = spec.exercise,
            version = spec.version,
            exercise = exercise(spec),
            testCases = spec.cases.map(::convertTestCase),

            exerciseClassName = spec.exercise.asClassName()
        )
    }

    return specifications
        .map(::convertSpecification)
}

fun ExerciseTestCase.asTestCall(): TestCall {
    val arguments: Map<String, AValue> = when {
        input.isEmpty() -> emptyMap()
        else -> TODO()
    }

    val result: AResult = when {
        expected is String -> AResult.Success(AString(expected))
        else -> TODO()
    }

    return TestCall(
        functionName = property,
        arguments = arguments,
        result = result
    )
}

fun generateExercises(config: Config, specifications: List<ImprovedExerciseSpecification>) {
    val moshi = moshi()
        .adapter(ImprovedExerciseSpecification::class.java)
        .indent("  ")

    fun generateExercise(spec: ImprovedExerciseSpecification) {
        val dir = config.generatedExercisesDir
            .resolve(spec.slug)
            .also { it.mkdirs() }

        writeBuildGradleSource(dir)
        writeSources(dir, spec)
        writeTestSources(dir, spec)
        copyAssets(assetsDir = config.assetsDir, outDir = dir)
    }

    fun generateExerciseDebugJson(specification: ImprovedExerciseSpecification) {
        val dir = config.generatedExercisesDir
            .resolve(specification.slug)
            .also { it.mkdirs() }

        val testFile = dir.resolve("__debug.json")
        moshi.toJson(specification)
            .let { testFile.writeText(it) }
    }

    specifications
        .onEach { println("\n'${it.slug}':") }
        .onEach(::generateExerciseDebugJson)
        .forEach(::generateExercise)
}

data class ExerciseSpecification(
    val exercise: String,
    val version: String,
    val cases: List<ExerciseTestCase> = emptyList()
)

data class ExerciseTestCase(
    val description: String,
    val property: String,
    val input: Map<String, Any?>,
    val expected: String
)

data class ImprovedExerciseSpecification(
    val slug: String,
    val version: String,
    val exercise: ImprovedExercise,
    val testCases: List<ImprovedTestCase>,

    val exerciseClassName: String,
    val testClassName: String = "${exerciseClassName}Test"
)

data class ImprovedExercise(
    //val hasEnclosedClass: Boolean

    val functions: List<FunctionCall>
)

data class FunctionCall(
    val functionName: String,
    val arguments: Map<String, AType>,
    val resultType: AType
) {

    sealed class AType {
        object AString : AType()
        object AUnit : AType()
    }
}

data class ImprovedTestCase(
    val name: String,
    val description: String,
    val call: TestCall
)

data class TestCall(
    val functionName: String,
    val arguments: Map<String, AValue>,
    val result: AResult
) {

    sealed class AResult {
        //        data class Exception(val e: Throwable) : AResult()

        data class Exception(val implementMe: String = "NotImplemented") : AResult()
        data class Success(val value: AValue) : AResult()
    }

    sealed class AValue {
        data class AString(val value: String) : AValue()
    }
}

fun AValue.stringify(): String = when (this) {
    is AString -> "\"$value\""
    else -> TODO()
}

fun String.simplifyForName() = this
    .toLowerCase()
    .filter { it.isLetterOrDigit() || it == ' ' }

fun String.asClassName() = this
    .toLowerCase()
    .filter { it.isLetterOrDigit() || it == '-' }
    .split('-')
    .joinToString("") { it.capitalize() }

fun moshi() = Moshi.Builder()
    .add(PolymorphicJsonAdapterFactory.of(AType::class.java, "__type")
        .withSubtype(AType.AString::class.java, AString::class.java.simpleName)
        .withSubtype(AType.AUnit::class.java, AUnit::class.java.simpleName)
    )
    .add(PolymorphicJsonAdapterFactory.of(AResult::class.java, "__type")
        .withSubtype(AResult.Success::class.java, AResult.Success::class.java.simpleName)
        .withSubtype(AResult.Exception::class.java, AResult.Exception::class.java.simpleName)
    )
    .add(PolymorphicJsonAdapterFactory.of(AValue::class.java, "__type")
        .withSubtype(AString::class.java, AString::class.java.simpleName)
    )
    .build()
