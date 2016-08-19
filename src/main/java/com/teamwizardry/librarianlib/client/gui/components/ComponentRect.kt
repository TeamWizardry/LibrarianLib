package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.client.util.Color
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.gui.Gui

/**
 * Created by TheCodeWarrior
 */
class ComponentRect(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentRect>(posX, posY, width, height) {

    val color = Option<ComponentRect, Color>(Color.WHITE)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        Gui.drawRect(pos.xi, pos.yi, pos.xi + size.xi, pos.yi + size.yi, color.getValue(this).hexARGB())
    }
}
