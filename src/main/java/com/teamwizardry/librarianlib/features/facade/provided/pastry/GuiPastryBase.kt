package com.teamwizardry.librarianlib.features.facade.provided.pastry

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground

open class GuiPastryBase: GuiBase() {
    val bg = PastryBackground(BackgroundTexture.DEFAULT, 0, 0, 0, 0)

    init {
        bg.zIndex = GuiLayer.BACKGROUND_Z
        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            bg.frame = main.bounds.grow(4.0)
        }
        main.add(bg)
    }
}