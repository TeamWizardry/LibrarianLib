package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import net.minecraft.util.text.ITextComponent

open class GuiPastryBase(title: ITextComponent): FacadeScreen(title) {
    val bg = PastryBackground(BackgroundTexture.DEFAULT, 0, 0, 0, 0)

    init {
        bg.zIndex = GuiLayer.BACKGROUND_Z
        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            bg.frame = main.bounds.grow(4.0)
        }
        main.add(bg)
    }
}