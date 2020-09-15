package com.teamwizardry.librarianlib.core.util.lerp

public class InvalidTypeException: RuntimeException {
    public constructor(): super()
    public constructor(message: String?): super(message)
    public constructor(message: String?, cause: Throwable?): super(message, cause)
    public constructor(cause: Throwable?): super(cause)
}

public class LerperNotFoundException: RuntimeException {
    public constructor(): super()
    public constructor(message: String?): super(message)
    public constructor(message: String?, cause: Throwable?): super(message, cause)
    public constructor(cause: Throwable?): super(cause)
}