@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.math.Vec3d
import java.awt.Color

inline fun <reified T: IVertexBuilder> T.pos(pos: Vec3d): T = this.pos(pos.x, pos.y, pos.z) as T
inline fun <reified T: IVertexBuilder> T.normal(normal: Vec3d): T = this.normal(normal.x, normal.y, normal.z)
inline fun <reified T: IVertexBuilder> T.tex(uv: Vec2d): T = this.tex(uv.x, uv.y)

inline fun <reified T: IVertexBuilder> T.pos(x: Number, y: Number, z: Number): T = this.pos(x.toDouble(), y.toDouble(), z.toDouble()) as T
inline fun <reified T: IVertexBuilder> T.normal(x: Number, y: Number, z: Number): T = this.normal(x.toFloat(), y.toFloat(), z.toFloat()) as T
inline fun <reified T: IVertexBuilder> T.tex(u: Number, v: Number): T = this.tex(u.toFloat(), v.toFloat()) as T

inline fun <reified T: IVertexBuilder> T.color(color: Color): T = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f) as T
