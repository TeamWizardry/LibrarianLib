package com.teamwizardry.librarianlib.facade.testmod.screens.pastry

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.facade.pastry.Pastry
import com.teamwizardry.librarianlib.facade.pastry.components.PastryButton
import com.teamwizardry.librarianlib.facade.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestButton
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestDropdown
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestProgress
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestScroll
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestSwitches
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestTabs
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.PastryTestTooltips
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests.*

class PastryTestScreen : FacadeTestScreen("Pastry") {
    val tests: Map<Class<*>, String> = mutableMapOf(
        PastryTestButton::class.java to "Button",
        PastryTestDropdown::class.java to "Dropdown",
        PastryTestProgress::class.java to "Progress",
        PastryTestSwitches::class.java to "Switches",
        PastryTestTooltips::class.java to "Tooltips",
        PastryTestScroll::class.java to "Scroll Pane",
        PastryTestTabs::class.java to "Tabs",
        Any::class.java to "<Fix commas in git diffs>"
    ).also { it.remove(Any::class.java) }

    val selector = StackLayout.build()
        .vertical()
        .alignTop()
        .alignCenterX()
        .fit()
        .also { selector ->
            tests.forEach { (clazz, name) ->
                val label = PastryLabel(0, 0, name)
                val item = GuiLayer(0, 0, label.widthi, Pastry.lineHeight)
                item.add(label)
                item.BUS.hook<GuiLayerEvents.LayoutChildren> {
                    label.frame = item.bounds
                }
                item.BUS.hook<GuiLayerEvents.MouseClick> {
                    selectTest(clazz, name)
                }
                selector.add(item)
            }
        }
        .build()

    val background = PastryBackground(0, 0, 1, 1)
    val contentBackground = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
    val contentArea = GuiLayer()
    val selectorArea = GuiLayer()
    val selectedTestLabel = PastryLabel(0, 1, "")
    val selectTestButton = PastryButton("Select Test", 1, 1) {
        selectorArea.isVisible = true
        contentArea.isVisible = false
    }
    var test = GuiLayer()

    init {
        main.size = vec(200, 200)

        main.add(background, contentBackground, contentArea, selectorArea, selectTestButton, selectedTestLabel)

        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            background.frame = main.bounds.offset(-2, -2, 2, 2)
            contentBackground.frame = main.bounds.offset(0, selectTestButton.frame.maxY + 1, 0, 0)

            selectedTestLabel.frame = rect(
                selectTestButton.frame.maxX + 5, 1,
                main.width - selectTestButton.width - 2, Pastry.lineHeight
            )
            contentArea.frame = contentBackground.frame.offset(2, 2, -2, -2)
            selectorArea.frame = contentBackground.frame.offset(2, 2, -2, -2)
        }


        contentArea.add(test)
        contentArea.BUS.hook<GuiLayerEvents.LayoutChildren> {
            test.frame = contentArea.bounds
        }
        contentArea.isVisible = false

        selectorArea.add(selector)
        selectorArea.BUS.hook<GuiLayerEvents.LayoutChildren> {
            selector.frame = selectorArea.bounds
        }

    }

    fun selectTest(testClass: Class<*>, testName: String) {
        selectorArea.isVisible = false
        contentArea.isVisible = true

        contentArea.remove(this.test)
        this.test = testClass.newInstance() as GuiLayer
        contentArea.add(this.test)
        contentArea.markLayoutDirty()
        selectedTestLabel.text = testName
    }
}
