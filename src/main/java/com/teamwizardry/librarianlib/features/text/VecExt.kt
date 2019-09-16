package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import games.thecodewarrior.bitfont.utils.Vec2i

fun Vec2i.toLL(): Vec2d = vec(x, y)
fun Vec2d.toBit(): Vec2i = Vec2i(xi, yi)
