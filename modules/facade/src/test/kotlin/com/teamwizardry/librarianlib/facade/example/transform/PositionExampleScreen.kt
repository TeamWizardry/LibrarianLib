package com.teamwizardry.librarianlib.facade.example.transform

import com.teamwizardry.librarianlib.facade.example.visualization.*
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.util.text.ITextComponent
import java.awt.Color

class PositionExampleScreen(title: ITextComponent): TransformExampleScreen(title) {
    init {
        val box = RectLayer(Color.LIGHT_GRAY, 0, 0, 20, 20)
        box.add(PositionVisualizationLayer())
        origin.add(box)
        topCrosshairs.isVisible = false

        box.pos_rm.animateKeyframes(vec(0, 0))
            .hold(5f)
            .add(15f, Easing.easeOutQuad, vec(50, 0))
            .add(15f, Easing.easeInOutQuad, vec(50, 50))
            .add(30f, Easing.easeInOutQuad, vec(-50, -50))
            .add(15f, Easing.easeInOutQuad, vec(0, -50))
            .add(15f, Easing.easeInQuad, vec(0, 0))
            .repeat()
    }
}