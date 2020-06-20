package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import java.awt.Color

class ZIndexTestScreen: FacadeTestScreen("Render to Quad Scale") {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 100, 100)
        main.size = bg.size
        main.add(bg)

        val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16).getSprite("")
        val stone = Mosaic("minecraft:textures/block/stone.png".toRl(), 16, 16).getSprite("")
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