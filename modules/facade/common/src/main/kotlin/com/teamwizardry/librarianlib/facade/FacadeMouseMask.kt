package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.math.Vec2d

public interface FacadeMouseMask {
    public fun isMouseMasked(mouseX: Double, mouseY: Double): Boolean
}