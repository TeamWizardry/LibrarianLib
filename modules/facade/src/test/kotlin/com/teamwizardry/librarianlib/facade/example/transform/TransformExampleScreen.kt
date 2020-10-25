package com.teamwizardry.librarianlib.facade.example.transform

import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.example.visualization.CrosshairsLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import net.minecraft.util.text.ITextComponent
import java.awt.Color

open class TransformExampleScreen(title: ITextComponent): FacadeScreen(title) {
    val origin: GuiLayer = GuiLayer()
    val bottomCrosshairs: CrosshairsLayer
    val topCrosshairs: CrosshairsLayer

    init {
        val color = DistinctColors.blue
        bottomCrosshairs = CrosshairsLayer(color)
        bottomCrosshairs.zIndex = -20.0

        val topColor = Color(color.red, color.green, color.blue, 127)
        topCrosshairs = CrosshairsLayer(topColor)
        topCrosshairs.zIndex = 20.0

        origin.add(bottomCrosshairs, topCrosshairs)

        main.add(origin)
    }
}