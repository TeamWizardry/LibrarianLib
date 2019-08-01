package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastrySwitch
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTabPage
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTabPane
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryToggle
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@UseExperimental(ExperimentalPastryAPI::class, ExperimentalBitfont::class)
class PastryTestTabs: PastryTestBase() {
    init {
        val tabPane = PastryTabPane()
        val pages = listOf(
            NamedPage("one"),
            NamedPage("two"),
            NamedPage("three"),
            NamedPage("+∞⁰"),
            NamedPage("-∞⁰")
        )
        tabPane.add(*pages.toTypedArray())
        pages[1].selected = true
        tabPane.frame = rect(0, 30, 150, 150)
        stack.add(tabPane)

        pages[3].isVisible = false
        pages[4].isVisible = false
        fun toggle(index: Int, label: String) {
            stack.add(StackLayout.build(150, 0)
                .horizontal().alignCenterY().spacing(2)
                .add(
                    PastrySwitch().also { switch ->
                        switch.state = pages[index].isVisible
                        switch.hook<PastryToggle.StateChangedEvent> { e ->
                            pages[index].isVisible = switch.state
                        }
                    },
                    PastryLabel(label)
                )
                .fitBreadth()
                .component()
            )
        }
        toggle(3, "Give me more power!")
        toggle(4, "o no, too much power.")
    }

    class NamedPage(name: String) : PastryTabPage(name) {
        val stack = StackLayout.build().align(Align2d.CENTER).layer()
        val nameLabel = PastryLabel("Page $name content")

        init {
            contents.add(stack)
            stack.add(nameLabel)
        }

        override fun layoutChildren() {
            super.layoutChildren()
            stack.frame = contents.bounds
        }
    }
}