package com.teamwizardry.librarianlib.client.guicontainer

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.vec

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent<ComponentSlot>(x, y) {
    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
    }

    override fun onTick() {
        val p = parent!!.unTransformRoot(this, vec(0,0))

        slot.xPos = p.xi
        slot.yPos = p.yi
    }
}
