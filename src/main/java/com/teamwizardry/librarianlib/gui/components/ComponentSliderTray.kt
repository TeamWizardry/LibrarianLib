package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.TickCounter
import com.teamwizardry.librarianlib.math.Vec2d

class ComponentSliderTray(posX: Int, posY: Int, internal var offsetX: Int, internal var offsetY: Int) : GuiComponent<ComponentSliderTray>(posX, posY) {

    internal var animatingIn = true
    internal var animatingOut = false
    internal var tickStart: Int = 0

    var lifetime = 5
    internal var currentOffsetX: Float = 0.toFloat()
    internal var rootPos: Vec2d

    init {
        setCalculateOwnHover(false)
        tickStart = TickCounter.ticks
        this.currentOffsetX = pos.x.toFloat()
        rootPos = pos
    }

    fun close() {
        tickStart = TickCounter.ticks
        animatingIn = false
        animatingOut = true
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // TODO: Respect partialTicks
        val t = (TickCounter.ticks - tickStart).toFloat() / lifetime.toFloat()
        if (t > 1) {
            if (animatingIn)
                animatingIn = false
        }

        if (Math.signum(offsetX.toFloat()) < 0) {
            if (animatingIn)
                if (currentOffsetX >= offsetX) currentOffsetX -= (-offsetX - Math.abs(currentOffsetX)) / 3
            if (animatingOut) {
                if (currentOffsetX < rootPos.x && currentOffsetX + (-offsetX - Math.abs(currentOffsetX)) / 3 < rootPos.x)
                    currentOffsetX += (-offsetX - Math.abs(currentOffsetX)) / 3
                else
                    invalidate()
            }

            // TODO: untested math.signum(x) < 0
        } else if (Math.signum(offsetX.toFloat()) < 0) {
            if (animatingIn)
                if (currentOffsetX > rootPos.x && currentOffsetX - (offsetX - Math.abs(currentOffsetX)) / 3 > rootPos.x)
                    currentOffsetX -= (offsetX - Math.abs(currentOffsetX)) / 3
                else
                    invalidate()
            if (animatingOut) {
                if (currentOffsetX <= offsetX) currentOffsetX += (offsetX - Math.abs(currentOffsetX)) / 3
            }

        } else
            invalidate()

        pos = Vec2d(rootPos.x + currentOffsetX, rootPos.y)
    }
}
