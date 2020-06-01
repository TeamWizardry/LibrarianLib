package com.teamwizardry.librarianlib.core.util.lerp

class InvalidTypeException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

class LerperNotFoundException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}