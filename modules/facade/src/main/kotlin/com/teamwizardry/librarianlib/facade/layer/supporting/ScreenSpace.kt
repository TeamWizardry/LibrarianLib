package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dView
import com.teamwizardry.librarianlib.math.MutableMatrix3d

/**
 * The logical pixel coordinate space for GUIs. Any layer within a GUI can convert points to [ScreenSpace] to get an
 * objective location on the display (measured in logical pixels at Minecraft's GUI scale, not literal screen pixels).
 */
public object ScreenSpace: CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D = DisplaySpace

    private val _transform = MutableMatrix3d()
    private val _inverseTransform = MutableMatrix3d()

    override val transform: Matrix3d = Matrix3dView(_transform)
        get() {
            updateMatrices()
            return field
        }
    override val inverseTransform: Matrix3d = Matrix3dView(_transform)
        get() {
            updateMatrices()
            return field
        }

    private fun updateMatrices() {
        val s = Client.guiScaleFactor
        val S = 1.0/s
        _transform.set(
            S, 0.0, 0.0,
            0.0, S, 0.0,
            0.0, 0.0, 1.0
        )
        _inverseTransform.set(
            s, 0.0, 0.0,
            0.0, s, 0.0,
            0.0, 0.0, 1.0
        )
    }
}