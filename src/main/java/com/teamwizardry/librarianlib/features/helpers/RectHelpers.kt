@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d

inline fun rect(x: Number, y: Number, width: Number, height: Number) = Rect2d(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
inline fun rect(pos: Vec2d, width: Number, height: Number) = Rect2d(pos.x, pos.y, width.toDouble(), height.toDouble())
inline fun rect(x: Number, y: Number, size: Vec2d) = Rect2d(x.toDouble(), y.toDouble(), size.x, size.y)
inline fun rect(pos: Vec2d, size: Vec2d) = Rect2d(pos.x, pos.y, size.x, size.y)
