package com.teamwizardry.librarianlib.core.util

class IncompleteBuilderException: RuntimeException {
    constructor(): super()
    constructor(missingProperties: List<String>): super(makeMessage("Missing properties: ", missingProperties))

    constructor(message: String): super(message)
    constructor(message: String, missingProperties: List<String>): super(makeMessage(message, missingProperties))

    constructor(message: String, cause: Throwable): super(message, cause)
    constructor(message: String, missingProperties: List<String>, cause: Throwable): super(makeMessage(message, missingProperties), cause)

    constructor(cause: Throwable): super(cause)
    constructor(missingProperties: List<String>, cause: Throwable): super(makeMessage("Missing properties: ", missingProperties), cause)

    private companion object {
        fun makeMessage(message: String, missingProperties: List<String>): String {
            return message + missingProperties.joinToString(", ")
        }
    }
}