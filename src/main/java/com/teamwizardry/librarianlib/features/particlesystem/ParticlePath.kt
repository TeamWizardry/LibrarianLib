package com.teamwizardry.librarianlib.features.particlesystem

/**
 * Defines a path a particle can follow, or a series of tangents for use as velocity vectors
 */
interface ParticlePath {
    /**
     * Returns the position at the passed fraction [t] along the path.
     *
     * @param particle The particle array
     * @param t (0–1) The fraction along the path the returned point should be at.
     * @param index (0–2) The index of the component (X/Y/Z) that is being requested
     */
    fun getPosition(particle: DoubleArray, t: Double, index: Int): Double

    /**
     * Returns the tangent direction at the passed fraction [t] along the path. The tangent may or may not have a length of 1,
     * though if it doesn't its length should represent the appropriate speed of the particle (e.g. slower on the two
     * ends of an ellipse) and not a simple calculation short-cut
     *
     * @param particle The particle array
     * @param t (0–1) The fraction along the path the returned tangent should be at.
     * @param index (0–2) The index of the component (X/Y/Z) that is being requested
     */
    fun getTangent(particle: DoubleArray, t: Double, index: Int): Double
}