package com.teamwizardry.librarianlib.features.neoguicontainer

import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.math.coordinatespaces.UnrelatedCoordinateSpaceException

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent(x, y) {
    val background = GuiComponent(0, 0)
    var scaler: GuiComponent? = null

    init {
        this.add(background)
    }

    override fun draw(partialTicks: Float) {
        background.isVisible = slot.stack.isEmpty
        slot.visible = true
    }

    @Hook
    @Suppress("UNUSED_PARAMETER")
    fun onTick(e: GuiComponentEvents.ComponentTickEvent) {
        try {
            val p = convertPointTo(Vec2d.ZERO, ContainerSpace)

            slot.xPos = p.xi
            slot.yPos = p.yi
        } catch(e: UnrelatedCoordinateSpaceException) {
            // nop
        }
    }
}
