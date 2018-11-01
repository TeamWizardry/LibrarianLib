package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

class PastryTextField(fontRenderer: FontRenderer, x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    constructor(x: Int, y: Int, width: Int, height: Int) : this(Minecraft.getMinecraft().fontRenderer, x, y, width, height)

    val field = ComponentTextField(fontRenderer, 1, 1, width-2, height-2)

    private val background = SpriteLayer(PastryTexture.textField, 0, 0, width, height)

    init {
        this.add(background, field)
        field.useUnderscoreCursor = false
    }

    override fun layoutChildren() {
        super.layoutChildren()
        background.size = this.size
        field.pos = vec(1,1)
        field.size = this.size - vec(2,2)
    }
}