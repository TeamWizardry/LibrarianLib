package com.teamwizardry.librarianlib.facade.layer

public class LayerHierarchyException: RuntimeException {
    internal constructor(): super()
    internal constructor(message: String?): super(message)
    internal constructor(message: String?, cause: Throwable?): super(message, cause)
    internal constructor(cause: Throwable?): super(cause)
}
