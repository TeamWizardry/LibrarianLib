package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.layers.PastrySwitch
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryTabPage
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryTabPane
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryToggle
import com.teamwizardry.librarianlib.math.Align2d
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase

@OptIn(ExperimentalPastryAPI::class)
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
                        switch.hook<PastryToggle.StateChangedEvent> {
                            pages[index].isVisible = switch.state
                        }
                    },
                    PastryLabel(label)
                )
                .fitBreadth()
                .build()
            )
        }
        toggle(3, "Give me more power!")
        toggle(4, "o no, too much power.")
    }

    class NamedPage(name: String): PastryTabPage(name) {
        val stack = StackLayout.build().align(Align2d.CENTER).build()
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