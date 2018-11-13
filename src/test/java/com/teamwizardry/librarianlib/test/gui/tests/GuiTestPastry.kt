package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryBackground
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryCheckbox
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryRadioButtonSet
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryToggle
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastrySwitch
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.kotlin.MainThreadExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by TheCodeWarrior
 */
class GuiTestPastry : GuiBase() {
    init {
        main.size = vec(200, 200)

        main.add(PastryBackground(0, 0, 200, 200))

        var button = PastryButton(5, 5, 100)
        button.label.text = "Push me! gg."
        main.add(button)
        button = PastryButton(5, 25, 100)
        button.label.text = "I may or may not be a bit too long to fit."
        main.add(button)

        val switch = PastrySwitch(120, 10)
        main.add(switch)

        val switches = switchPanel()
        switches.x = -switches.width
        switches.y = 30.0
        switches.add(PastryBackground(0, 0, switches.widthi, switches.heighti).also { it.zIndex = -1 })
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
