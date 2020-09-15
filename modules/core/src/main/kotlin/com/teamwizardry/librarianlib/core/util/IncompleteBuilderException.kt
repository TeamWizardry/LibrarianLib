package com.teamwizardry.librarianlib.core.util

/**
 * A general-purpose exception for when a builder was finalized before some required options were set
 */
public class IncompleteBuilderException: RuntimeException {
    public constructor(): super()
    public constructor(missingProperties: List<String>): super(makeMessage("Missing properties: ", missingProperties))

    public constructor(message: String): super(message)
    public constructor(message: String, missingProperties: List<String>): super(makeMessage(message, missingProperties))

    public constructor(message: String, cause: Throwable): super(message, cause)
    public constructor(message: String, missingProperties: List<String>, cause: Throwable): super(makeMessage(message, missingProperties), cause)

    public constructor(cause: Throwable): super(cause)
    public constructor(missingProperties: List<String>, cause: Throwable): super(makeMessage("Missing properties: ", missingProperties), cause)

    private companion object {
        fun makeMessage(message: String, missingProperties: List<String>): String {
            return message + missingProperties.joinToString(", ")
        }
    }
}