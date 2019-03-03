package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.features.math.Rect2d

inline fun rect(x: Number, y: Number, width: Number, height: Number) = Rect2d(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
