package com.teamwizardry.librarianlib.facade.test.screens.textinput

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.*
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.awt.Color

class SimpleTextInputTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 200)
        val toggle = TextLayer(1, 1, "Windows")
        toggle.fitToText(TextFit.BOTH)
        toggle.color = Color.black
        var useMac = MinecraftClient.IS_SYSTEM_MAC

        val input = TextInputLayer(10, 10, 180, 180)
        input.text.insert(0, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        input.text.insert(12, "red ", BitfontFormatting.color to Color.RED)
        input.setCursor(input.text.length)

        toggle.text = if(useMac) "macOS" else "Windows"
        input.inputLayout = if(useMac) MacInputLayout else WindowsInputLayout
        toggle.hook<GuiLayerEvents.MouseDown> {
            if(toggle.mouseOver) {
                useMac = !useMac
                toggle.text = if (useMac) "macOS" else "Windows"
                input.inputLayout = if (useMac) MacInputLayout else WindowsInputLayout
            }
        }

        main.size = bg.size
        main.add(bg, toggle, input)
    }
}