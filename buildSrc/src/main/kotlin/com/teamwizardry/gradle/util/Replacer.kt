package com.teamwizardry.gradle.util

import java.util.regex.Matcher
import java.util.regex.MatchResult

class Replacer {
    private val replacements = mutableListOf<Replacement>()

    fun add(oldString: String, newString: String) {
        replacements.add(Replacement.Literal(oldString, newString))
    }

    fun add(oldRegex: Regex, newString: String) {
        replacements.add(Replacement.PatternString(oldRegex, newString))
    }

    fun add(oldRegex: Regex, newString: (MatchResult) -> String) {
        replacements.add(Replacement.PatternCallback(oldRegex, newString))
    }

    fun apply(input: String): String {
        for(replacement in replacements) {
            replacement.matcher.reset(input)
        }

        val builder = StringBuilder(input.length) // the output is probably going to have a similar length
        val buffer = StringBuffer()

        var start = 0

        while(start < input.length) {
            var match: Replacement? = null

            for(replacement in replacements) {
                if(replacement.matcher.find(start)) {
                    if(match == null || replacement.matcher.start() < match.matcher.start()) {
                        match = replacement
                    }
                }
            }

            if(match == null) {
                builder.append(input, start, input.length)
                break
            }

            builder.append(input, start, match.matcher.start())
            when(match) {
                is Replacement.Literal -> builder.append(match.newString)
                is Replacement.PatternString -> {
                    match.matcher.appendReplacement(buffer, match.newString)
                    builder.append(buffer)
                    buffer.setLength(0)
                }
                is Replacement.PatternCallback -> {
                    builder.append(match.result(match.matcher.toMatchResult()))
                }
            }
            start = match.matcher.end()
        }

        return builder.toString()
    }

    sealed class Replacement(regex: Regex) {
        val matcher: Matcher = regex.toPattern().matcher("")

        class Literal(oldString: String, val newString: String): Replacement(Regex.escape(oldString).toRegex())
        class PatternString(pattern: Regex, val newString: String): Replacement(pattern)
        class PatternCallback(pattern: Regex, val result: (MatchResult) -> String): Replacement(pattern)
    }
}