package com.teamwizardry.librarianlib.client.gui.template

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.GuiStyle
import com.teamwizardry.librarianlib.client.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.common.util.vec

class ButtonTemplate(style: GuiStyle, posX: Int, posY: Int) : ComponentTemplate<ComponentVoid>(ComponentVoid(posX, posY)) {

    protected var style: GuiStyle? = null
    protected var tiled: ComponentSpriteTiled
    var contents: ComponentVoid

    init {

        contents = ComponentVoid(style.BUTTON_BORDER, style.BUTTON_BORDER)

        tiled = ComponentSpriteTiled(style.BUTTON, style.BUTTON_BORDER, 0, 0)
        tiled.size = vec(0, 0)

        result.add(tiled)
        result.add(contents)

        tiled.BUS.hook(GuiComponent.PreDrawEvent::class.java) { event ->
            val box = contents.getLogicalSize()
            tiled.size = vec((box?.width() ?: 0.0) - 1 + 2 * style.BUTTON_BORDER, (box?.height() ?: 0.0) - 1 + 2 * style.BUTTON_BORDER)
        }
    }

}
