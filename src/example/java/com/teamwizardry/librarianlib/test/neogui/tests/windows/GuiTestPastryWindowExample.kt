package com.teamwizardry.librarianlib.test.neogui.tests.windows

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.neogui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d

class GuiTestPastryWindowExample: PastryWindow(200, 100) {
    val valueText = TextLayer(0, 0, 0, 12)
    val openDialogButton = PastryButton("New text", 0, 0, 60)
    val closeButton = PastryButton("X", 1, 1, 12)
    val moarButton = PastryButton("Moar", 0, 0, 40)

    init {
        this.title = "Such example, many wow"

        valueText.anchor = vec(0.5, 0)
        valueText.align = Align2d.TOP_CENTER
        valueText.wrap = true

        openDialogButton.label.align = Align2d.CENTER
        openDialogButton.anchor = vec(0.5, 0.5)

        moarButton.anchor = vec(1, 0)

        content.add(valueText, openDialogButton, closeButton, moarButton)

        minSize = vec(50, 50)
        maxSize = vec(400, 300)

        setNeedsLayout()
        wireEvents()
    }

    fun wireEvents() {
        openDialogButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            println(this.debugPrint(true))
            GuiTestPastryWindowDialog({
                valueText.text = "Bzzzzt!"
            }, {
                valueText.text = it
            }).open()
        }
        moarButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            GuiTestPastryWindowExample().open()
        }
        closeButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            this.close()
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()
        moarButton.pos = vec(header.width-1, 1)

        valueText.pos = vec(content.size.x/2, 12)
        valueText.width = content.width
        openDialogButton.pos = vec(content.size.x/2, content.size.y - 12)
    }
}

private class GuiTestPastryWindowDialog(
    val failureHandler: () -> Unit, val successHandler: (text: String) -> Unit
): PastryWindow(120, 75) {
    val valueField = ComponentTextField(10, 20, 100, 12)
    val fieldBackground = SpriteLayer(PastryTexture.textfield, 8, 18, 104, 16)
    val okButton = PastryButton("OK", 5, 40, 50)
    val cancelButton = PastryButton("Cancel", 65, 40, 50)

    init {
        this.title = "Enter text"

        okButton.label.align = Align2d.CENTER
        cancelButton.label.align = Align2d.CENTER

        content.add(fieldBackground, valueField, okButton, cancelButton)

        wireEvents()
    }

    fun wireEvents() {
        var hasRun = false
        valueField.BUS.hook<ComponentTextField.TextSentEvent> {
            successHandler(it.content)
            hasRun = true
            this.close()
        }
        okButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            successHandler(valueField.text)
            hasRun = true
            this.close()
        }
        cancelButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            hasRun = true
            failureHandler()
            this.close()
        }
        this.BUS.hook<LoseFocusEvent> {
            println(this.debugPrint(true))
            if(!hasRun) failureHandler()
            this.close()
        }
    }
}
