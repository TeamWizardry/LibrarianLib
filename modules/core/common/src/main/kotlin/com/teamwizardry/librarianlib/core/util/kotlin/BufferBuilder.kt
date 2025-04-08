@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import java.awt.Color

public inline fun <reified T: VertexConsumer> T.vertex(pos: Vec3d): T = this.vertex(pos.x, pos.y, pos.z) as T
public inline fun <reified T: VertexConsumer> T.normal(normal: Vec3d): T = this.normal(normal.x, normal.y, normal.z)
public inline fun <reified T: VertexConsumer> T.texture(uv: Vec2d): T = this.texture(uv.x, uv.y)

public inline fun <reified T: VertexConsumer> T.vertex(x: Number, y: Number, z: Number): T = this.vertex(x.toFloat(), y.toFloat(), z.toFloat()) as T
public inline fun <reified T: VertexConsumer> T.normal(x: Number, y: Number, z: Number): T = this.normal(x.toFloat(), y.toFloat(), z.toFloat()) as T
public inline fun <reified T: VertexConsumer> T.texture(u: Number, v: Number): T = this.texture(u.toFloat(), v.toFloat()) as T

public inline fun <reified T: VertexConsumer> T.color(color: Color): T = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f) as T

public inline fun <reified T: VertexConsumer> T.vertex(matrix: Matrix4d, pos: Vec3d): T = this.vertex(
    matrix.transformX(pos.x, pos.y, pos.z),
    matrix.transformY(pos.x, pos.y, pos.z),
    matrix.transformZ(pos.x, pos.y, pos.z)
) as T
public inline fun <reified T: VertexConsumer> T.vertex(matrix: Matrix4d, x: Number, y: Number, z: Number): T = this.vertex(
    matrix.transformX(x.toDouble(), y.toDouble(), z.toDouble()),
    matrix.transformY(x.toDouble(), y.toDouble(), z.toDouble()),
    matrix.transformZ(x.toDouble(), y.toDouble(), z.toDouble())
) as T
public inline fun <reified T: VertexConsumer> T.vertex(matrix: Matrix4f, x: Number, y: Number, z: Number): T {
    val imatrix = matrix
    val xd = x.toFloat()
    val yd = y.toFloat()
    val zd = z.toFloat()
    return this.vertex(
        imatrix.m00() * xd + imatrix.m01() * yd + imatrix.m02() * zd + imatrix.m03() * 1,
        imatrix.m10() * xd + imatrix.m11() * yd + imatrix.m12() * zd + imatrix.m13() * 1,
        imatrix.m20() * xd + imatrix.m21() * yd + imatrix.m22() * zd + imatrix.m23() * 1
    ) as T
}

/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(pos: Vec2d): T = this.vertex(pos.x, pos.y, 0.0) as T
/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(x: Number, y: Number): T = this.vertex(x.toDouble(), y.toDouble(), 0.0) as T

/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(matrix: Matrix4d, x: Number, y: Number): T = this.vertex(
    matrix.transformX(x.toDouble(), y.toDouble(), 0.0),
    matrix.transformY(x.toDouble(), y.toDouble(), 0.0),
    matrix.transformZ(x.toDouble(), y.toDouble(), 0.0)
) as T


/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(matrix: Matrix4d, pos: Vec2d): T = this.vertex2d(matrix, pos.x, pos.y)

/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(stack: MatrixStack, x: Number, y: Number): T {
    val matrix = stack.peek().positionMatrix
    this.vertex(
        matrix.m00() * x.toFloat() + matrix.m01() * y.toFloat() + matrix.m02() * 0f + matrix.m03() * 1,
        matrix.m10() * x.toFloat() + matrix.m11() * y.toFloat() + matrix.m12() * 0f + matrix.m13() * 1,
        matrix.m20() * x.toFloat() + matrix.m21() * y.toFloat() + matrix.m22() * 0f + matrix.m23() * 1,
    )
    return this
}

/** Sets the position with Z = 0 */
public inline fun <reified T: VertexConsumer> T.vertex2d(stack: MatrixStack, pos: Vec2d): T = this.vertex2d(stack, pos.x, pos.y)
