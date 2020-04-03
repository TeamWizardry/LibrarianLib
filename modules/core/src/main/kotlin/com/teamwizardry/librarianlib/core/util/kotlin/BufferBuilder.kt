@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.Vec3d
import java.awt.Color

inline fun IVertexBuilder.pos(pos: Vec3d): IVertexBuilder = this.pos(pos.x, pos.y, pos.z)
inline fun IVertexBuilder.normal(normal: Vec3d): IVertexBuilder = this.normal(normal.x, normal.y, normal.z)
inline fun IVertexBuilder.tex(uv: Vec2d): IVertexBuilder = this.tex(uv.x, uv.y)

inline fun IVertexBuilder.pos(x: Number, y: Number, z: Number): IVertexBuilder = this.pos(x.toDouble(), y.toDouble(), z.toDouble())
inline fun IVertexBuilder.normal(x: Number, y: Number, z: Number): IVertexBuilder = this.normal(x.toFloat(), y.toFloat(), z.toFloat())
inline fun IVertexBuilder.tex(u: Number, v: Number): IVertexBuilder = this.tex(u.toFloat(), v.toFloat())

inline fun IVertexBuilder.color(color: Color): IVertexBuilder = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
