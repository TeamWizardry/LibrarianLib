package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
@UseExperimental(ExperimentalBitfont::class)
class GuiTestTextLayer : GuiBase() {
    init {
        main.size = vec(300, 300)

        val background = RectLayer(Color.WHITE, 0, 0, 300, 300)
        main.add(background)

        var panel: RectLayer
        panel = RectLayer(Color.LIGHT_GRAY, 0, 0, 100, 300)
        main.add(panel)

        var textLayer: TextLayer
        textLayer = TextLayer(0, 0, 100, 35)
        textLayer.text = """
            <No wrap> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.wrap = false
        textLayer.fitToText = true
        main.add(textLayer)

        textLayer = TextLayer(0, 120, 100, 35)
        textLayer.text = """
            <Wrap> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.fitToText = true
        main.add(textLayer)

        panel = RectLayer(Color.LIGHT_GRAY, 110, 100, 150, 150)
        main.add(panel)

        textLayer = TextLayer(110, 100, 150, 150)
        textLayer.text = """
            <Center> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nibh sem, interdum ut nunc eu,
            <newline> imperdiet molestie nibh. Etiam sit amet maximus ante sodales.
        """.trimIndent()
        textLayer.align = Align2d.CENTER
        main.add(textLayer)
    }
}
