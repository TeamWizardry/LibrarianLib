package com.teamwizardry.librarianlib.test.particlesystem.particlemaker

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastrySlider
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastryToggle
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import net.minecraft.util.math.MathHelper
import kotlin.math.roundToInt

class ComponentFreeSlider(text: String, posY: Int, width: Int, beginValue: Double, sliderRange: ClosedRange<Double>, numFieldRange: ClosedRange<Double>, stateChange: (newProgress: Double) -> Unit) : GuiComponent(0, posY, width, 20) {

    val numberLayer: ComponentDescriptiveNumField

    init {
        val titleLayer = TextLayer(0, -10, 0, 0)
        titleLayer.fitToText = true
        titleLayer.text = text

        val slider = PastrySlider(0, posY + 10, width - 30, false, Cardinal2d.GUI.DOWN)
        slider.range = sliderRange
        slider.value = beginValue
        slider.BUS.fire(PastryToggle.StateChangeEvent())

        numberLayer = ComponentDescriptiveNumField(null, beginValue, slider.widthi + 10, 0, 40, 20) { field, progress ->
            val result = MathHelper.clamp((progress * 100).roundToInt() / 100.0, numFieldRange.start, numFieldRange.endInclusive)
            field.text = "$result"
            stateChange(result)
        }
        numberLayer.text = "${(slider.value * 100).roundToInt() / 100.0}"
        slider.BUS.hook<PastrySlider.ValueChangeEvent> { event ->
            val result = (event.newValue * 100).roundToInt() / 100.0
            numberLayer.text = "$result"
            stateChange(result)
        }

        slider.add(titleLayer, numberLayer)
        add(slider)
    }

    fun updateText(text: String) {
        numberLayer.updateText(text)
    }
}