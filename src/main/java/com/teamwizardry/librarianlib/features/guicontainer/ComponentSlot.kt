package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent<ComponentSlot>(x, y) {
    val background = ComponentVoid(0, 0)
    var scaler: GuiComponent<*>? = null

    init {
        this.add(background)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        background.isVisible = slot.stack.isEmpty
        slot.visible = true
    }

    override fun onTick() {
        val p = parent!!.unTransformRoot(this, Vec2d.ZERO)

        if (scaler == null) scaler = this.root.children.firstOrNull()
        val s = scaler?.pos?: Vec2d.ZERO

        slot.xPos = p.xi - s.xi
        slot.yPos = p.yi - s.yi
    }
}
