package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueFloat
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

class ComponentSliderTray(posX: Int, posY: Int, offsetX: Int, offsetY: Int) : GuiComponent(posX, posY) {

    internal var offsetX: Int by RMValue(offsetX)
    internal var offsetY: Int by RMValue(offsetY)
    internal var animatingIn = true
    internal var animatingOut = false
    internal var tickStart: Int = 0

    var lifetime = 5
    internal var currentOffsetX: Float by RMValueFloat(0.toFloat())
    internal var rootPos: Vec2d by RMValue(Vec2d.ZERO)

    init {
        shouldCalculateOwnHover = false
        tickStart = ClientTickHandler.ticks
        this.currentOffsetX = pos.x.toFloat()
        rootPos = pos
    }

    fun close() {
        tickStart = ClientTickHandler.ticks
        animatingIn = false
        animatingOut = true
    }

    override fun draw(partialTicks: Float) {
        // TODO: Respect partialTicks
        val t = (ClientTickHandler.ticks - tickStart).toFloat() / lifetime.toFloat()
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

        pos = vec(rootPos.x + currentOffsetX, rootPos.y)
    }
}
