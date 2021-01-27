package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.util.math.vector.Vector3d

public class SimpleCoordinateSpace2D: CoordinateSpace2D {
    override var parentSpace: CoordinateSpace2D? = null

    public var pos: Vec2d = vec(0, 0)
    public var scale2d: Vec2d = vec(1, 1)
    public var scale: Double
        get() = (scale2d.x + scale2d.y) / 2
        set(value) {
            scale2d = vec(value, value)
        }
    public var rotation: Double = 0.0

    override var transform: Matrix3d = Matrix3d.IDENTITY
        get() {
            updateMatrixIfNeeded()
            return field
        }
        private set

    override var inverseTransform: Matrix3d = Matrix3d.IDENTITY
        get() {
            updateMatrixIfNeeded()
            return field
        }
        private set

    private data class MatrixParams(val pos: Vec2d = Vec2d.ZERO,
        val rotation: Quaternion = Quaternion.IDENTITY, val scale: Vector3d = vec(1, 1, 1),
        val inverseRotation: Quaternion = Quaternion.IDENTITY, val inverseScale: Vector3d = vec(1, 1, 1))

    private var matrixParams = MatrixParams()

    private fun createMatrix() {
        val matrix = MutableMatrix3d()
        matrix.translate(matrixParams.pos)
        matrix.rotate(matrixParams.rotation)
        matrix.scale(matrixParams.scale)
        this.transform = matrix.toImmutable()

        matrix.set(Matrix3d.IDENTITY)
        matrix.scale(matrixParams.inverseScale)
        matrix.rotate(matrixParams.rotation)
        matrix.translate(-matrixParams.pos)
        this.inverseTransform = matrix.toImmutable()
    }

    private fun updateMatrixIfNeeded() {
        val inverseScale = vec(
            if (scale2d.x == 0.0) Double.POSITIVE_INFINITY else 1.0 / scale2d.x,
            if (scale2d.y == 0.0) Double.POSITIVE_INFINITY else 1.0 / scale2d.y,
            1
        )
        val rotation = Quaternion.fromAngleDegAxis(rotation, 0.0, 1.0, 0.0)
        val newParams = MatrixParams(pos,
            rotation, vec(scale2d.x, scale2d.y, 1),
            rotation.invert(), inverseScale
        )

        if (newParams != matrixParams) {
            matrixParams = newParams
            createMatrix()
        }
    }
}