package com.teamwizardry.librarianlib.glitter

/**
 * Defines a path a particle can follow, or a series of tangents for use as velocity vectors
 */
interface ParticlePath {
    /**
     * The position or tangent that was just computed
     */
    val value: DoubleArray

    /**
     * Computes the position at the passed fraction [t] along the path.
     *
     * @param particle The particle array
     * @param t (0–1) The fraction along the path the returned point should be at.
     */
    fun computePosition(particle: DoubleArray, t: Double)

    /**
     * Computes the tangent direction at the passed fraction [t] along the path. The tangent may or may not have a length of 1,
     * though if it doesn't its length should represent the appropriate speed of the particle (e.g. slower on the two
     * ends of an ellipse) and not a simple calculation short-cut
     *
     * @param particle The particle array
     * @param t (0–1) The fraction along the path the returned tangent should be at.
     */
    fun computeTangent(particle: DoubleArray, t: Double)
}