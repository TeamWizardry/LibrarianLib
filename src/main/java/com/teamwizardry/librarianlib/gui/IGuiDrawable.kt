package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.math.Vec2d

/**
 * An object that can be drawn to a bookcomponents
 * @author Pierce Corcoran
 */
interface IGuiDrawable {

    /**
     * Draw this object to the screen.
     * @param mousePos
     * *
     * @param partialTicks
     */
    fun draw(mousePos: Vec2d, partialTicks: Float)

}
