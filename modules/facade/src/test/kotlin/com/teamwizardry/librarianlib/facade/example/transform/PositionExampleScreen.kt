package com.teamwizardry.librarianlib.facade.example.transform

import com.teamwizardry.librarianlib.facade.example.visualization.*
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.util.text.ITextComponent
import java.awt.Color

class PositionExampleScreen(title: ITextComponent): TransformExampleScreen(title) {
    init {
        val box = RectLayer(Color.LIGHT_GRAY, 0, 0, 20, 20)
        origin.add(PositionVisualizationLayer(box))
        origin.add(box)
        topCrosshairs.isVisible = false

        box.pos_rm.animateKeyframes(vec(0, 0))
            .hold(10f)
            .add(15f, Easing.easeInOutQuad, vec(50, 0))
            .hold(5f)
            .add(15f, Easing.easeInOutQuad, vec(50, 50))
            .hold(5f)
            .add(30f, Easing.easeInOutQuad, vec(-50, -50))
            .hold(5f)
            .add(15f, Easing.easeInOutQuad, vec(0, -50))
            .hold(5f)
            .add(15f, Easing.easeInOutQuad, vec(0, 0))
            .repeat()
    }
}