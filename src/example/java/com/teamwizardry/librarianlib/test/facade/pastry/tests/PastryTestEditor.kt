package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTextEditor
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@UseExperimental(ExperimentalBitfont::class, ExperimentalPastryAPI::class)
class PastryTestEditor: PastryTestBase() {
    private val words = """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed bibendum nibh ut finibus interdum. \
Quisque aliquam, quam eget semper gravida, nisi mauris dapibus arcu, sit amet sodales sem eros quis sapien. \
Curabitur sodales ex sed nisl dignissim egestas. Phasellus est felis, dapibus non volutpat ut, semper id enim. \
Suspendisse potenti. Maecenas eleifend sem sed arcu volutpat, et commodo enim feugiat. \
Etiam commodo, urna sit amet imperdiet viverra, eros augue commodo nisi, nec congue ex ipsum et orci. \
Aenean egestas, ligula quis aliquet commodo, massa tellus dignissim mauris, a rhoncus nisl ex vel nibh. 
    """.trimIndent().split("\\s+".toRegex()).map { " $it " }

    init {
        this.label("Edit:")
        val field = PastryTextEditor(0, 0, 150, 50)
        this.stack.add(field)
        this.label("(Randomly inserts or deletes text when the mouse enters its bounds)")

        field.hook<GuiComponentEvents.MouseEnterEvent> {
            val text = field.attributedText.mutableCopy()
            if(text.length != 0 && Random.nextBoolean()) {
                val start = Random.nextInt(text.length)
                val end = start + (Random.nextInt(10) - 5)
                val range = max(0, min(start, end)) until min(text.length, max(start, end))
                text.delete(range.first, range.last+1)
            } else {
                val toInsert = words.random()
                val insertionPoint = if(text.length == 0) 0 else Random.nextInt(text.length)
                text.insert(insertionPoint, toInsert)
            }
            field.attributedText = text
        }
    }
}