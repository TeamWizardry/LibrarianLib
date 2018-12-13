package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextTestLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestTextLayer : GuiBase() {
    init {
        main.size = vec(300, 300)

        val background = ColorLayer(Color.WHITE, 0, 0, 300, 300)
        main.add(background)

        var panel: ColorLayer
        panel = ColorLayer(Color.LIGHT_GRAY, 0, 0, 100, 300)
        main.add(panel)

        var textLayer: TextLayer
        textLayer = TextLayer(0, 0, 100, 35)
        textLayer.text = """
            <No wrap> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam aliquet sapien non justo finibus, sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.wrap = false
        textLayer.fitToText = true
        main.add(textLayer)

        textLayer = TextLayer(0, 120, 100, 35)
        textLayer.text = """
            <Wrap> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam aliquet sapien non justo finibus, sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.fitToText = true
        main.add(textLayer)

        panel = ColorLayer(Color.LIGHT_GRAY, 110, 100, 150, 150)
        main.add(panel)

        textLayer = TextLayer(110, 100, 150, 150)
        textLayer.text = """
            <Center> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam aliquet sapien non justo finibus, sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.align = Align2d.CENTER
        main.add(textLayer)
    }
}
