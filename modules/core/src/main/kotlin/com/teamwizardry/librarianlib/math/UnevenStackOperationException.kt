package com.teamwizardry.librarianlib.math

import java.lang.RuntimeException

class UnevenStackOperationException: RuntimeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
    constructor(cause: Throwable): super(cause)
}