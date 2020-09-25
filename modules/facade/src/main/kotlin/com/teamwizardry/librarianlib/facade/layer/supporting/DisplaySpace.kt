package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d

/**
 * The uppermost coordinate space for GUIs. Any component within a GUI can convert points to [ScreenSpace] to get an
 * objective location on the display (measured in literal screen pixels).
 */
public object DisplaySpace: CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D? = null
    override val transform: Matrix3d
        get() = Matrix3d.IDENTITY
    override val inverseTransform: Matrix3d
        get() = Matrix3d.IDENTITY
}