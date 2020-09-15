package com.teamwizardry.librarianlib.foundation.util

public open class FoundationSerializerException: RuntimeException {
    internal constructor(): super()
    internal constructor(message: String): super(message)
    internal constructor(message: String, cause: Throwable?): super(message, cause)
    internal constructor(cause: Throwable): super(cause)
}

public class InvalidSerializedClassException: FoundationSerializerException {
    internal constructor(): super()
    internal constructor(message: String): super(message)
    internal constructor(message: String, cause: Throwable?): super(message, cause)
    internal constructor(cause: Throwable): super(cause)
}
