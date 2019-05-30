package com.teamwizardry.librarianlib.features.neogui.provided

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.vec
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color
import kotlin.math.max

@UseExperimental(ExperimentalBitfont::class)
class GuiSafetyNetError(e: Exception): GuiBase() {
    init {
        val border = 8
        main.width = max(300.0, TextLayer.stringSize(e.javaClass.simpleName).width+border*2)
        val width = main.widthi - border*2

        val builder = StackLayout.build()
            .space(2)
            .alignCenterX()
            .width(width)
            .fit()

        val title = TextLayer(0, 0, "§4§nSafety net caught an exception:")
        val className = TextLayer(0, 0, e.javaClass.simpleName)
        className.fitToText()
        builder.add(title, className)

        e.message?.also { text ->
            val messageWrap = GuiLayer()
            val message = TextLayer(0, 0, width, 0)
            message.wrap = true
            message.text = text
            message.fitToText()
            messageWrap.size = message.size
            if(message.lineCount == 1)
                messageWrap.width = TextLayer.stringSize(text, width).width
            messageWrap.add(message)
            val divider = ColorLayer(Color.darkGray, 0, 0, width, 1)
            builder.add(divider, messageWrap)
        }

        val stack = builder.layer()
        main.height = stack.height + border*2
        stack.pos = vec(border, border)

        val bg = ColorLayer(Color.lightGray, 0, 0, main.widthi, main.heighti)
        main.add(bg, stack)
    }
}