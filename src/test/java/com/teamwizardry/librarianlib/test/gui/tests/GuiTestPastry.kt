package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.GuiBase
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
        main.size = vec(300, 300)

        main.add(PastryBackground(0, 0, 300, 300))

        var button = PastryButton(5, 5, 100)
        button.label.text = "Push me! gg."
        main.add(button)
        button = PastryButton(5, 25, 100)
        button.label.text = "I may or may not be a bit too long to fit."
        main.add(button)

        val switch = PastrySwitch(120, 10)
        main.add(switch)

        val radioSet = PastryRadioButtonSet<String>()

        main.add(radioSet.addOption("First", 120, 20))
        main.add(radioSet.addOption("Second", 120, 30))
        main.add(radioSet.addOption("Third", 120, 40))
        main.add(radioSet.addOption("Fourth", 120, 50))
        val radioText = TextLayer(130, 20, 0, 0)
        radioText.text = "<none>"
        radioText.fitToText = true
        main.add(radioText)

        radioSet.BUS.hook<PastryRadioButtonSet<String>.OptionSelected> {
            radioText.text = it.option ?: "<none>"
        }
    }
}
