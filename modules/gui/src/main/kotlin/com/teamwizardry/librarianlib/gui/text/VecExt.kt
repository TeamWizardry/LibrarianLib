package com.teamwizardry.librarianlib.gui.text

import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.utils.Vec2i

fun Vec2i.toLL(): Vec2d = vec(x, y)
fun Vec2d.toBit(): Vec2i = Vec2i(xi, yi)
