package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ScheduledRepeatedCallbacksTestScreen(title: Text): FacadeScreen(title) {
    init {
        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 16, 16).getSprite("")
        val stone = Mosaic(Identifier("minecraft:textures/block/stone.png"), 16, 16).getSprite("")
        val layer = SpriteLayer(dirt, 0, 0, 64, 64)

        layer.delay(0f, 40f) {
            layer.sprite = dirt
        }
        layer.delay(20f, 40f) {
            layer.sprite = stone
        }
        main.size = layer.size
        main.add(layer)
    }
}