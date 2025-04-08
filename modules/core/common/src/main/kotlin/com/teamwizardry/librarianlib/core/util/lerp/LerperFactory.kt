package com.teamwizardry.librarianlib.core.util.lerp

import dev.thecodewarrior.mirror.type.TypeMirror

/**
 * A factory that can create specialized lerpers for types that match the defined [pattern]. A type matches the pattern
 * if `pattern.isAssignableFrom(theType)`. When deciding the factory to use, the [LerperMatcher] object selects the
 * one with the most [specific][TypeMirror.specificity] pattern. In the case of equal specificity it chooses the last
 * one registered, in order to support overriding.
 */
public abstract class LerperFactory<T: Lerper<*>>(public val matcher: LerperMatcher, public val pattern: TypeMirror, public val predicate: ((TypeMirror) -> Boolean)? = null) {
    public abstract fun create(mirror: TypeMirror): T
}
