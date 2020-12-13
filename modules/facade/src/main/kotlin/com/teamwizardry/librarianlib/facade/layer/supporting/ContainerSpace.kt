package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dView
import com.teamwizardry.librarianlib.math.MutableMatrix3d

/**
 * The "main" pixel coordinate space for container GUIs. Any component within a container can convert points to
 * [ContainerSpace] to get the location within the "main", centered coordinate system.
 */
public object ContainerSpace: CoordinateSpace2D {
    public var guiLeft: Int = 0
    public var guiTop: Int = 0

    override val parentSpace: CoordinateSpace2D = ScreenSpace

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
        _transform.set(
            1.0, 0.0, -guiLeft.toDouble(),
            0.0, 1.0, -guiTop.toDouble(),
            0.0, 0.0, 1.0
        )
        _inverseTransform.set(
            1.0, 0.0, guiLeft.toDouble(),
            0.0, 1.0, guiTop.toDouble(),
            0.0, 0.0, 1.0
        )
    }
}