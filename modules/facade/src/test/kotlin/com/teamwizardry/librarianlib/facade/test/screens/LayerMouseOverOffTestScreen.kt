package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class LayerMouseOverOffTestScreen(title: Text): FacadeScreen(title) {
    init {
        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 16, 16).getSprite("")
        val stone = Mosaic(Identifier("minecraft:textures/block/stone.png"), 16, 16).getSprite("")
        val layer = SpriteLayer(dirt)
        layer.pos = vec(32, 32)

        layer.BUS.hook<GuiLayerEvents.MouseMoveOver> {
            layer.sprite = stone
        }
        layer.BUS.hook<GuiLayerEvents.MouseMoveOff> {
            layer.sprite = dirt
        }
        facade.root.add(layer)
    }
}