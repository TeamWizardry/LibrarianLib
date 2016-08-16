package com.teamwizardry.librarianlib.gui.template

import com.teamwizardry.librarianlib.gui.GuiStyle
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d

class ButtonTemplate(style: GuiStyle, posX: Int, posY: Int) : ComponentTemplate<ComponentVoid>() {

    protected var style: GuiStyle? = null
    protected var tiled: ComponentSpriteTiled
    var contents: ComponentVoid

    init {

        result = ComponentVoid(posX, posY)
        contents = ComponentVoid(style.BUTTON_BORDER, style.BUTTON_BORDER)

        tiled = ComponentSpriteTiled(style.BUTTON, style.BUTTON_BORDER, 0, 0)
        tiled.size = Vec2d(0.0, 0.0)

        result!!.add(tiled)
        result!!.add(contents)

        tiled.preDraw.add({ c, pos, ticks ->
            val box = contents.getLogicalSize()
            tiled.size = Vec2d((box?.width() ?: 0.0) - 1 + 2 * style.BUTTON_BORDER, (box?.height() ?: 0.0) - 1 + 2 * style.BUTTON_BORDER)
        })
    }

}
