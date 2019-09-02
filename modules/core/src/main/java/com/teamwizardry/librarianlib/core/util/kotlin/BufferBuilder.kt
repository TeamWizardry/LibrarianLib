@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.math.Vec3d
import java.awt.Color

fun BufferBuilder.pos(pos: Vec3d): BufferBuilder = this.pos(pos.x, pos.y, pos.z)
fun BufferBuilder.tex(uv: Vec2d): BufferBuilder = this.tex(uv.x, uv.y)

inline fun BufferBuilder.pos(x: Number, y: Number, z: Number): BufferBuilder = this.pos(x.toDouble(), y.toDouble(), z.toDouble())
inline fun BufferBuilder.tex(u: Number, v: Number): BufferBuilder = this.tex(u.toDouble(), v.toDouble())

fun BufferBuilder.color(color: Color): BufferBuilder = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
