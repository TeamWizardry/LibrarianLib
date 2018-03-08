package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class ComponentSearchResults(private val book: IBookGui) : NavBarHolder(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32, book), ISearchAlgorithm.Acceptor {

    private val margin = 16
    override var currentActive: GuiComponent? = null
    private val pageHeader = ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
    private var resultSection: ComponentVoid = ComponentVoid(0, margin, size.xi, size.yi - margin)

    private var results: List<ISearchAlgorithm.Result>? = mutableListOf()

    override val bookParent: Book
        get() = book.book

    init {
        pageHeader.text.setValue(I18n.format("librarianlib.book.results.notfound"))
        pageHeader.unicode.setValue(true)
        pageHeader.wrap.setValue(size.xi)
        add(pageHeader)
        add(resultSection)
    }

    override fun acceptResults(results: List<ISearchAlgorithm.Result>?) {
        this.results = results
        reset()

        navBar = ComponentNavBar(book, size.xi / 2 - 35, size.yi + 16, 70, 1)
        add(navBar)

        navBar.BUS.hook(EventNavBarChange::class.java, { update() })

        if (results == null || results.isEmpty()) {
            pageHeader.text.setValue(I18n.format("${LibrarianLib.MODID}.book.results.notfound"))
        } else {
            val forTyping = results[0]
            if (forTyping.isSpecificResult)
                pageHeader.text.setValue(if (results.size == 1)
                    I18n.format("${LibrarianLib.MODID}.book.results.oneresult")
                else
                    I18n.format("${LibrarianLib.MODID}.book.results.nresults", results.size))
            else
                pageHeader.text.setValue(I18n.format("${LibrarianLib.MODID}.book.results.toobroad", results.size))

            var pageComponent = ComponentVoid(0, 0, size.xi, size.yi)
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
            var count = 0
            for (resultItem in results) {
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

                    exactResult = I18n.format("librarianlib.book.results.match", matchPercentage)
                    simplifiedResult = I18n.format("librarianlib.book.results.match", Math.round(matchPercentage))
                } else {
                    color = TextFormatting.RESET
                    simplifiedResult = if (resultItem.frequency == 1.0)
                        I18n.format("librarianlib.book.results.kwd")
                    else
                        I18n.format("librarianlib.book.results.kwds", resultItem.frequency.toInt())
                    exactResult = simplifiedResult
                }

                textComponent.unicode.setValue(true)
                textComponent.text.setValue("| " + color + simplifiedResult)

                indexButton.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) { textComponent.text.setValue("  | " + color + TextFormatting.ITALIC + exactResult) }

                indexButton.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) { textComponent.text.setValue("| " + color + simplifiedResult) }
                // --------- HANDLE EXTRA TEXT COMPONENT --------- //

                count++
                if (count >= itemsPerPage) {
                    navBar.maxPages++
                    pages.add(pageComponent)
                    pageComponent = ComponentVoid(0, 0, size.xi, size.yi)
                    resultSection.add(pageComponent)
                    pageComponent.isVisible = false
                    count = 0
                }
            }

            navBar.whenMaxPagesSet()
        }
    }

    private fun reset() {
        resultSection.invalidate()
        resultSection = ComponentVoid(0, margin, size.xi, size.yi - margin)
        add(resultSection)
        pages.clear()
        navBar.invalidate()
    }

    public override fun update() {
        if (currentActive != null) currentActive!!.isVisible = false

        currentActive = pages[navBar.page]

        if (currentActive != null) currentActive!!.isVisible = true
    }

    override fun createComponent(book: IBookGui): GuiComponent {
        val results = ComponentSearchResults(book)
        results.acceptResults(this.results)
        return results
    }
}
