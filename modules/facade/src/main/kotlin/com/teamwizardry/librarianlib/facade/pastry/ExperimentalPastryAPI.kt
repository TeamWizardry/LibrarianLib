package com.teamwizardry.librarianlib.facade.pastry

/**
 * Used for highly volatile APIs in Pastry.
 *
 * The entirety of Pastry is experimental on some level, but including experimental annotations literally everywhere
 * would get tiresome pretty quick, so much of it is unannotated. However, some components are so volatile that they
 * necessitate a compile-time check, so that's what this is for.
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalPastryAPI