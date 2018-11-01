package com.teamwizardry.librarianlib.features.math.coordinatespaces

import com.teamwizardry.librarianlib.features.math.Matrix3

object ScreenSpace: CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D? = null
    override val matrix: Matrix3
        get() = Matrix3.identity
    override val inverseMatrix: Matrix3
        get() = Matrix3.identity
}