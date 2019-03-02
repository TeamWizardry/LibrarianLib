package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.math.coordinatespaces.UnrelatedCoordinateSpaceException

/**
 * Created by TheCodeWarrior
 */
class ComponentSlot(val slot: SlotBase, x: Int, y: Int) : GuiComponent(x, y) {
    val background = ComponentVoid(0, 0)
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
            val p = convertPointTo(Vec2d.ZERO, ScreenSpace)

            if (scaler == null) scaler = this.gui?.subComponents?.firstOrNull()
            val s = scaler?.pos ?: Vec2d.ZERO

            slot.xPos = p.xi - s.xi
            slot.yPos = p.yi - s.yi
        } catch(e: UnrelatedCoordinateSpaceException) {
            // nop
        }
    }
}
