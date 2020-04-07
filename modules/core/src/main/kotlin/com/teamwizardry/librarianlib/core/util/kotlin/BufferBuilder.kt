@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.util.math.Vec3d
import java.awt.Color

inline fun <reified T: IVertexBuilder> T.pos(pos: Vec3d): T = this.pos(pos.x, pos.y, pos.z) as T
inline fun <reified T: IVertexBuilder> T.normal(normal: Vec3d): T = this.normal(normal.x, normal.y, normal.z)
inline fun <reified T: IVertexBuilder> T.tex(uv: Vec2d): T = this.tex(uv.x, uv.y)

inline fun <reified T: IVertexBuilder> T.pos(x: Number, y: Number, z: Number): T = this.pos(x.toDouble(), y.toDouble(), z.toDouble()) as T
inline fun <reified T: IVertexBuilder> T.normal(x: Number, y: Number, z: Number): T = this.normal(x.toFloat(), y.toFloat(), z.toFloat()) as T
inline fun <reified T: IVertexBuilder> T.tex(u: Number, v: Number): T = this.tex(u.toFloat(), v.toFloat()) as T

inline fun <reified T: IVertexBuilder> T.color(color: Color): T = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f) as T

inline fun <reified T: IVertexBuilder> T.pos(matrix: Matrix4d, pos: Vec3d): T = this.pos(
    matrix.transformX(pos.x, pos.y, pos.z),
    matrix.transformY(pos.x, pos.y, pos.z),
    matrix.transformZ(pos.x, pos.y, pos.z)
) as T
inline fun <reified T: IVertexBuilder> T.pos(matrix: Matrix4d, x: Number, y: Number, z: Number): T = this.pos(
    matrix.transformX(x.toDouble(), y.toDouble(), z.toDouble()),
    matrix.transformY(x.toDouble(), y.toDouble(), z.toDouble()),
    matrix.transformZ(x.toDouble(), y.toDouble(), z.toDouble())
) as T
inline fun <reified T: IVertexBuilder> T.pos(matrix: Matrix4f, x: Number, y: Number, z: Number): T {
    @Suppress("CAST_NEVER_SUCCEEDS") val imatrix = matrix as IMatrix4f
    val xd = x.toDouble()
    val yd = y.toDouble()
    val zd = z.toDouble()
    return this.pos(
        imatrix.m00 * xd + imatrix.m01 * yd + imatrix.m02 * zd + imatrix.m03 * 1,
        imatrix.m10 * xd + imatrix.m11 * yd + imatrix.m12 * zd + imatrix.m13 * 1,
        imatrix.m20 * xd + imatrix.m21 * yd + imatrix.m22 * zd + imatrix.m23 * 1
    ) as T
}

/** Sets the position with Z = 0 */
inline fun <reified T: IVertexBuilder> T.pos2d(pos: Vec2d): T = this.pos(pos.x, pos.y, 0.0) as T
/** Sets the position with Z = 0 */
inline fun <reified T: IVertexBuilder> T.pos2d(x: Number, y: Number): T = this.pos(x.toDouble(), y.toDouble(), 0.0) as T

/** Sets the position with Z = 0 */
inline fun <reified T: IVertexBuilder> T.pos2d(matrix: Matrix3d, pos: Vec2d): T = this.pos(
    matrix.transformX(pos.x, pos.y),
    matrix.transformY(pos.x, pos.y),
    0.0
) as T
/** Sets the position with Z = 0 */
inline fun <reified T: IVertexBuilder> T.pos2d(matrix: Matrix3d, x: Number, y: Number): T = this.pos(
    matrix.transformX(x.toDouble(), y.toDouble()),
    matrix.transformY(x.toDouble(), y.toDouble()),
    0.0
) as T
