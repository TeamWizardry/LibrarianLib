package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.util.text.ITextComponent

class SimpleSpriteTestScreen(title: ITextComponent): FacadeScreen(title) {
    init {
        val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16)
        val layer = SpriteLayer(dirt.getSprite(""))
        facade.root.add(layer)
    }
}