package com.teamwizardry.librarianlib.test.facade.pastry

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.Pastry
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.facade.pastry.tests.PastryTestButton
import com.teamwizardry.librarianlib.test.facade.pastry.tests.PastryTestDropdown
import com.teamwizardry.librarianlib.test.facade.pastry.tests.PastryTestProgress
import com.teamwizardry.librarianlib.test.facade.pastry.tests.PastryTestSwitches
import com.teamwizardry.librarianlib.test.facade.pastry.tests.PastryTestTooltips
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@UseExperimental(ExperimentalBitfont::class)
class GuiTestPastry : GuiBase() {
    val tests: Map<Class<*>, String> = mutableMapOf(
        PastryTestButton::class.java to "Button",
        PastryTestDropdown::class.java to "Dropdown",
        PastryTestProgress::class.java to "Progress",
        PastryTestSwitches::class.java to "Switches",
        PastryTestTooltips::class.java to "Tooltips",
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
                val item = GuiComponent(0, 0, label.widthi, Pastry.lineHeight)
                item.add(label)
                item.BUS.hook<GuiLayerEvents.LayoutChildren> {
                    label.frame = item.bounds
                }
                item.BUS.hook<GuiComponentEvents.MouseClickEvent> {
                    selectTest(clazz, name)
                }
                selector.add(item)
            }
        }
        .component()
    val background = PastryBackground(0, 0, 1, 1)
    val contentBackground = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
    val contentArea = GuiComponent()
    val selectorArea = GuiComponent()
    val selectedTestLabel = PastryLabel(0, 1, "")
    val selectTestButton = PastryButton("Select Test", 1, 1) {
        selectorArea.isVisible = true
        contentArea.isVisible = false
    }
    var test = GuiComponent()

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
        this.test = testClass.newInstance() as GuiComponent
        contentArea.add(this.test)
        contentArea.setNeedsLayout()
        selectedTestLabel.text = testName
    }
}
