package com.teamwizardry.librarianlib.math

/**
 * The uppermost coordinate space for GUIs. Any component within a GUI can convert points to [ScreenSpace] to get an
 * objective location on the display (measured in logical pixels at Minecraft's GUI scale, not literal screen pixels).
 */
object ScreenSpace: CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D? = null
    override val matrix: Matrix3d
        get() = Matrix3d.IDENTITY
    override val inverseMatrix: Matrix3d
        get() = Matrix3d.IDENTITY
}