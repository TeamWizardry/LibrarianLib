package com.teamwizardry.librarianlib.features.gui.provided

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.vec
import org.apache.commons.lang3.exception.ExceptionUtils
import java.awt.Color
import kotlin.math.max

class GuiSafetyNetError(e: Exception): GuiBase() {
    init {
        val border = 8
        main.width = max(300.0, TextLayer.stringSize(e.javaClass.simpleName).width+border*2)
        val width = main.widthi - border*2

        val title = TextLayer(0, 0, "§4§nSafety net caught an exception:")
        val className = TextLayer(0, 0, e.javaClass.simpleName)
        className.fitToText()

        val messageWrap = GuiLayer()
        val message = TextLayer(0, 0, width, 0)
        message.wrap = true
        e.message?.also { message.text = it }
        message.fitToText()
        messageWrap.height = message.height
        e.message?.also { messageWrap.width = TextLayer.stringSize(it, width).width }
        messageWrap.add(message)

        val classStack = StackLayout.build()
            .alignCenterX()
            .add(title, className)
            .space(5)
            .width(width)
            .fitLength()
            .layer()

        val divider = ColorLayer(Color.darkGray, 0, 0, width, 1)
        val stack = StackLayout.build()
            .space(2)
            .add(classStack, divider, messageWrap)
            .width(width)
            .fit()
            .also {
                if(message.height <= className.height*1.5) {
                    it.alignCenterX()
                }
            }
            .layer()

        main.height = stack.height + border*2
        stack.pos = vec(border, border)

        val bg = ColorLayer(Color.lightGray, 0, 0, main.widthi, main.heighti)
        main.add(bg, stack)
    }
}