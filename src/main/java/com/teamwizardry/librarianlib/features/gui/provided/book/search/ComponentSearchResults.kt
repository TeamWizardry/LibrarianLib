package com.teamwizardry.librarianlib.features.gui.provided.book.search

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting
import kotlin.math.ceil

class ComponentSearchResults(book: IBookGui, results: List<ISearchAlgorithm.Result>?, index: Int) : GuiComponent(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32) {

    companion object {
        fun numberOfPages(results: List<ISearchAlgorithm.Result>?): Int {
            if (results == null || results.isEmpty())
                return 1
            return ceil(results.size / 8.0).toInt()
        }
    }

    private val margin = 16
    private val pageHeader = ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
    private var resultSection: GuiComponent = GuiComponent(0, margin, size.xi, size.yi - margin)

    init {
        pageHeader.text = I18n.format("${LibrarianLib.MODID}.book.results.notfound")
        pageHeader.unicode = true
        pageHeader.wrap = size.xi
        add(pageHeader)
        add(resultSection)

        if (results != null && results.isNotEmpty()) {
            val forTyping = results[0]
            if (forTyping.isSpecificResult)
                pageHeader.text = if (results.size == 1)
                    I18n.format("${LibrarianLib.MODID}.book.results.oneresult")
                else
                    I18n.format("${LibrarianLib.MODID}.book.results.nresults", results.size)
            else
                pageHeader.text = I18n.format("${LibrarianLib.MODID}.book.results.toobroad", results.size)

            val pageComponent = GuiComponent(0, 0, size.xi, size.yi)
            resultSection.add(pageComponent)

            var largestFrequency = 0.0
            var smallestFrequency = Integer.MAX_VALUE.toDouble()
            for (resultItem in results) {
                if (resultItem.frequency > largestFrequency)
                    largestFrequency = resultItem.frequency
                if (resultItem.frequency < smallestFrequency)
                    smallestFrequency = resultItem.frequency
            }

            val itemsPerPage = 8
            var count = (-itemsPerPage * index)
            for (resultItem in results) {
                if (count < 0)
                    count++
                else {
                    val matchPercentage = Math.round(resultItem.frequency * 100.0).toDouble()
                    if (matchPercentage <= 0) continue

                    val resultEntry = resultItem.entry

                    val textComponent = ComponentText(25, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)

                    val indexButton = book.makeNavigationButton(count, resultEntry) { it.add(textComponent) }
                    pageComponent.add(indexButton)

                    // --------- HANDLE EXTRA TEXT COMPONENT --------- //
                    val color: TextFormatting
                    val exactResult: String
                    val simplifiedResult: String
                    if (forTyping.isSpecificResult) {

                        color = when {
                            matchPercentage <= 25 -> TextFormatting.DARK_RED
                            matchPercentage <= 50 -> TextFormatting.YELLOW
                            matchPercentage <= 75 -> TextFormatting.GREEN
                            else -> TextFormatting.DARK_GREEN
                        }

                        exactResult = I18n.format("${LibrarianLib.MODID}.book.results.match", matchPercentage)
                        simplifiedResult = I18n.format("${LibrarianLib.MODID}.book.results.match", Math.round(matchPercentage))
                    } else {
                        color = TextFormatting.RESET
                        simplifiedResult = if (resultItem.frequency == 1.0)
                            I18n.format("${LibrarianLib.MODID}.book.results.kwd")
                        else
                            I18n.format("${LibrarianLib.MODID}.book.results.kwds", resultItem.frequency.toInt())
                        exactResult = simplifiedResult
                    }

                    textComponent.unicode = true
                    textComponent.text = "| $color$simplifiedResult"

                    indexButton.BUS.hook(GuiComponentEvents.MouseMoveInEvent::class.java) { textComponent.text = "  | $color${TextFormatting.ITALIC}$exactResult" }

                    indexButton.BUS.hook(GuiComponentEvents.MouseMoveOutEvent::class.java) { textComponent.text = "| $color$simplifiedResult" }
                    // --------- HANDLE EXTRA TEXT COMPONENT --------- //

                    count++
                    if (count >= itemsPerPage)
                        break
                }
            }
        }
    }
}
