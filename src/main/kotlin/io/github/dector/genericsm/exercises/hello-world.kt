package io.github.dector.genericsm.exercises

import io.github.dector.genericsm.models.entry
import io.github.dector.genericsm.models.meta

fun `hello-world`() = meta {
    schema = "1.0"
    id = "hello-world"

    data {
        exercise {
            fileName = "HelloWorld.kt"
            content = "fun hello(): String = TODO()"
        }

        tests {
            name = "HelloWorldTest"
            entries {
                +entry {
                    name = "helloWorldTest"
                    content = """assertEquals("Hello, World!", hello())"""
                }
            }
        }
    }
}
