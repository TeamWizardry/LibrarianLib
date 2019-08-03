package com.teamwizardry.librarianlib.core.util.kotlin

import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.math.Vec3d
import java.awt.Color

fun BufferBuilder.pos(pos: Vec3d): BufferBuilder = this.pos(pos.x, pos.y, pos.z)
fun BufferBuilder.tex(uv: Vec2d): BufferBuilder = this.tex(uv.x, uv.y)
fun BufferBuilder.color(color: Color): BufferBuilder = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
