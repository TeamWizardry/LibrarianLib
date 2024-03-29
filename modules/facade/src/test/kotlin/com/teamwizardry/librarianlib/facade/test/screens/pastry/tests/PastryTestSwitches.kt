package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryCheckbox
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryRadioButtonSet
import com.teamwizardry.librarianlib.facade.pastry.layers.PastrySwitch
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.core.util.vec

@OptIn(ExperimentalPastryAPI::class)
class PastryTestSwitches: PastryTestBase() {
    init {
        this.size = vec(40, 40)

        val switches = switchPanel()
        stack.add(switches)
    }

    fun switchPanel(): GuiLayer {
        val panel = GuiLayer(0, 0, 55, 45)

        val radioSet = PastryRadioButtonSet<String>()
        panel.add(radioSet.addOption("1", 5, 5))
        panel.add(radioSet.addOption("2", 5, 15))
        panel.add(radioSet.addOption("3", 5, 25))
        panel.add(radioSet.addOption("4", 5, 35))

        val radioText = PastryLabel(13, 0, 0, 0)
        radioText.text = "x"
//        radioText.fitToText = true TODO
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