package com.teamwizardry.librarianlib.foundation.util

open class FoundationSerializerException: RuntimeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable): super(cause)
}

class InvalidSerializedClassException: FoundationSerializerException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable): super(cause)
}
