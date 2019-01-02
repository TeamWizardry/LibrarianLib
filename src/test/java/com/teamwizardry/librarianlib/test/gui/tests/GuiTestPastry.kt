package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastryCheckbox
import com.teamwizardry.librarianlib.features.gui.provided.pastry.layers.PastryProgressBar
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastryRadioButtonSet
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.PastrySwitch
import com.teamwizardry.librarianlib.features.helpers.vec

/**
 * Created by TheCodeWarrior
 */
class GuiTestPastry : GuiBase() {
    init {
        main.size = vec(200, 200)

        main.add(PastryBackground(0, 0, 200, 200))

        val progress = PastryProgressBar(10, 35, 75, 5)
        progress.progress_im.animateKeyframes(0.0)
            .add(80f, 1.0, Easing.easeOutCubic)
            .add(10f, 1.0)
            .add(40f, 0.0, Easing.easeOutBounce)
            .add(10f, 0.0)
            .finish().repeatCount = -1
        main.add(progress)

        val switches = switchPanel()
        switches.x = -switches.width
        switches.y = 30.0
        switches.add(PastryBackground(0, 0, switches.widthi, switches.heighti).also { it.zIndex = 0.0 })
        main.add(switches)
    }

    fun switchPanel(): GuiComponent {
        val panel = GuiComponent(0, 0, 55, 45)

        val radioSet = PastryRadioButtonSet<String>()
        panel.add(radioSet.addOption("1", 5, 5))
        panel.add(radioSet.addOption("2", 5, 15))
        panel.add(radioSet.addOption("3", 5, 25))
        panel.add(radioSet.addOption("4", 5, 35))

        val radioText = TextLayer(13, 5, 0, 0)
        radioText.text = "x"
        radioText.fitToText = true
        panel.add(radioText)

        radioSet.BUS.hook<PastryRadioButtonSet<String>.OptionSelected> {
            radioText.text = it.option ?: "x"
        }

        panel.add(PastryCheckbox(23, 5))
        panel.add(PastryCheckbox(23, 15))
        panel.add(PastryCheckbox(23, 25))
        panel.add(PastryCheckbox(23, 35))

        panel.add(PastrySwitch(35, 5))
        panel.add(PastrySwitch(35, 15))
        panel.add(PastrySwitch(35, 25))
        panel.add(PastrySwitch(35, 35))

        return panel
    }
}
