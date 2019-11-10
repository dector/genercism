object Deps {
    const val kotlin_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlin_coroutines}"
    const val kotlin_test = "io.kotlintest:kotlintest-runner-junit5:${Versions.kotlin_test}"

    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshi_adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshi_kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val hjson = "org.hjson:hjson:${Versions.hjson}"

    const val autodsl = "com.juanchosaravia.autodsl:annotation:${Versions.autodsl}"
    const val autodsl_processor = "com.juanchosaravia.autodsl:processor:${Versions.autodsl}"
    //const val kotlinpoet = "com.squareup:kotlinpoet:${Versions.kotlinepoet}"
}

object Versions {
    const val kotlin = "1.3.50"
    const val kotlin_coroutines = "1.3.2"
    const val kotlin_test = "3.4.2"

    const val moshi = "1.8.0"
    const val hjson = "3.0.0"
    const val autodsl = "0.0.9"
    //const val kotlinepoet = "1.4.0"
}
