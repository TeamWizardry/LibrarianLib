package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SimpleSpriteTestScreen(title: Text): FacadeScreen(title) {
    init {
        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 16, 16)
        val layer = SpriteLayer(dirt.getSprite(""))
        facade.root.add(layer)
    }
}