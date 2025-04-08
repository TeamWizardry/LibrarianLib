package com.teamwizardry.librarianlib.math

import java.lang.RuntimeException

public class UnevenStackOperationException: RuntimeException {
    public constructor(): super()
    public constructor(message: String): super(message)
    public constructor(message: String, cause: Throwable): super(message, cause)
    public constructor(cause: Throwable): super(cause)
}