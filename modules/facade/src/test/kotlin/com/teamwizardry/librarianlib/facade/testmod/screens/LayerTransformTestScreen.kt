package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.util.text.ITextComponent

class LayerTransformTestScreen(title: ITextComponent): FacadeScreen(title) {
    init {
        val dirt = Mosaic(loc("minecraft:textures/block/dirt.png"), 16, 16).getSprite("")
        val stone = Mosaic(loc("minecraft:textures/block/stone.png"), 16, 16).getSprite("")
        val layer = SpriteLayer(dirt)
        layer.pos = vec(32, 32)
        layer.rotation = Math.toRadians(15.0)

        val layer2 = SpriteLayer(dirt)
        layer2.pos = vec(32, 32)
        layer2.rotation = Math.toRadians(-15.0)
        layer.add(layer2)

        layer.BUS.hook<GuiLayerEvents.MouseMove> {
            layer.sprite = if (layer.mouseOver) stone else dirt
        }
        layer2.BUS.hook<GuiLayerEvents.MouseMove> {
            layer2.sprite = if (layer2.mouseOver) stone else dirt
        }
        facade.root.add(layer)
    }
}