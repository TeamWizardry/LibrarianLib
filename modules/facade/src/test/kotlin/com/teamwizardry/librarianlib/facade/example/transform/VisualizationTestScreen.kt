package com.teamwizardry.librarianlib.facade.example.transform

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.example.visualization.AnchorVisualizationLayer
import com.teamwizardry.librarianlib.facade.example.visualization.PositionVisualizationLayer
import com.teamwizardry.librarianlib.facade.example.visualization.ScaleVisualizationLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import net.minecraft.text.Text
import java.awt.Color

class VisualizationTestScreen(title: Text): TransformExampleScreen(title) {
    init {
        val box = RectLayer(Color.LIGHT_GRAY, -50, -30, 80, 80)
        box.anchor = vec(0.2, 0.2)
        box.scale2d = vec(2, 1)
        box.rotation = Math.toRadians(10.0)

        box.add(ScaleVisualizationLayer(20.0))
//        origin.add(BoundsVisualizationLayer(box))
        origin.add(AnchorVisualizationLayer(box))
        origin.add(PositionVisualizationLayer(box))

        origin.add(box)
    }
}