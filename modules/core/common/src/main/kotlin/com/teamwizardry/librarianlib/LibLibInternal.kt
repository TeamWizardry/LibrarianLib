package com.teamwizardry.librarianlib

@RequiresOptIn(message = "LibrarianLib internals, not for external use", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class LibLibInternal
