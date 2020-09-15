package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.utils.Vec2i

internal fun Vec2i.toLL(): Vec2d = vec(x, y)
internal fun Vec2d.toBit(): Vec2i = Vec2i(xi, yi)
