package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent(x, y) {
    val background = ComponentVoid(0, 0)
    var scaler: GuiComponent? = null

    init {
        this.add(background)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        background.isVisible = slot.stack.isEmpty
        slot.visible = true
    }

    //@Hook
    fun onTick(e: GuiComponentEvents.ComponentTickEvent) {
        val p = thisPosToOtherContext(null)

        if (scaler == null) scaler = this.root.children.firstOrNull()
        val s = scaler?.pos ?: Vec2d.ZERO

        slot.xPos = p.xi - s.xi
        slot.yPos = p.yi - s.yi
    }
}
