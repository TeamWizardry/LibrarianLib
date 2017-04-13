package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent<ComponentSlot>(x, y) {
    val background = ComponentVoid(0, 0)

    init {
        this.add(background)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        background.isVisible = slot.stack.isEmpty
        slot.visible = true
    }

    override fun onTick() {
        val p = parent!!.unTransformRoot(this, vec(0, 0))

        slot.xPos = p.xi
        slot.yPos = p.yi
    }
}
