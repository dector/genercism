package io.github.dector.genericsm.exercises

import io.github.dector.genericsm.models.entry
import io.github.dector.genericsm.models.meta

fun `reverse-string`() = meta {
    schema = "1.0"
    id = "reverse-string"

    data {
        exercise {
            fileName = "ReverseString.kt"
            content = "fun reverse(str: String): String = TODO()"
        }

        tests {
            name = "ReverseStringTest"
            entries {
                +entry {
                    name = "testAnEmptyString"
                    content = """assertEquals("", reverse(""))"""
                }
                +entry {
                    name = "testAWord"
                    content = """assertEquals("tobor", reverse("robot"))"""
                }
                +entry {
                    name = "testACapitalizedWord"
                    content = """assertEquals("nemaR", reverse("Ramen"))"""
                }
                +entry {
                    name = "testASentenceWithPunctuation"
                    content = """assertEquals("!yrgnuh m'I", reverse("I'm hungry!"))"""
                }
                +entry {
                    name = "testAPalindrome"
                    content = """assertEquals("racecar", reverse("racecar"))"""
                }
            }
        }
    }
}
