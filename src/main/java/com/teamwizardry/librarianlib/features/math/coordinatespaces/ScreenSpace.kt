package com.teamwizardry.librarianlib.features.math.coordinatespaces

import com.teamwizardry.librarianlib.features.math.Matrix3

/**
 * The uppermost coordinate space for GUIs. Any component within a GUI can convert points to [ScreenSpace] to get an
 * objective location on the display (measured in logical pixels at Minecraft's GUI scale, not literal screen pixels).
 */
object ScreenSpace: CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D? = null
    override val matrix: Matrix3
        get() = Matrix3.identity
    override val inverseMatrix: Matrix3
        get() = Matrix3.identity
}