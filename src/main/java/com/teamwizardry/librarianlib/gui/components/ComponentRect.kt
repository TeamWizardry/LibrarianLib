package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.Option
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.util.Color
import net.minecraft.client.gui.Gui

/**
 * Created by TheCodeWarrior
 */
class ComponentRect(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentRect>(posX, posY, width, height) {

    val color = Option<ComponentRect, Color>(Color.WHITE)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        Gui.drawRect(pos.xi, pos.yi, pos.xi + size.xi, pos.yi + size.yi, color.getValue(this)?.hexARGB() ?: 0x000000)
    }
}
