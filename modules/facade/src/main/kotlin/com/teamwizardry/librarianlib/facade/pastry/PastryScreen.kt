package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryDynamicBackground
import net.minecraft.text.Text

public open class PastryScreen(title: Text): FacadeScreen(title) {
    public val background: PastryDynamicBackground = PastryDynamicBackground(PastryBackgroundStyle.VANILLA, main)

    init {
        background.zIndex = GuiLayer.BACKGROUND_Z
        main.add(background)
    }
}