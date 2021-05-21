package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color

class ZIndexTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 100, 100)
        main.size = bg.size
        main.add(bg)

        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 16, 16).getSprite("")
        val stone = Mosaic(Identifier("minecraft:textures/block/stone.png"), 16, 16).getSprite("")
        val layer1 = SpriteLayer(dirt, 16, 16, 48, 48)
        val layer2 = SpriteLayer(stone, 36, 36, 48, 48)
        bg.zIndex = -10.0
        layer1.zIndex = 1.0
        layer2.zIndex = -1.0

        layer1.delay(20f, 20f) {
            val tmp = layer1.zIndex
            layer1.zIndex = layer2.zIndex
            layer2.zIndex = tmp
        }
        main.add(layer1, layer2)
    }
}