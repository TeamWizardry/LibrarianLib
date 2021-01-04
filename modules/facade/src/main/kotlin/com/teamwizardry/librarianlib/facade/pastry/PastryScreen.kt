package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryDynamicBackground
import net.minecraft.util.text.ITextComponent

public open class PastryScreen(title: ITextComponent): FacadeScreen(title) {
    public val background: PastryDynamicBackground = PastryDynamicBackground(PastryBackgroundStyle.VANILLA, main)

    init {
        background.zIndex = GuiLayer.BACKGROUND_Z
        main.add(background)
    }
}