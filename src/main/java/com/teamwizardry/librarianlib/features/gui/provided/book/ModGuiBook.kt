package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import java.awt.Color
import java.util.*

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
@Suppress("LeakingThis")
open class ModGuiBook(override val book: Book) : GuiBase(146, 180), IBookGui {
    override val cachedSearchContent = book.contentCache
    private var sheetRL = ResourceLocation(book.textureSheet)
    private var guideBookSheet = Texture(ResourceLocation(sheetRL.resourceDomain, "textures/" + sheetRL.resourcePath + ".png"))

    override var bindingSprite: Sprite = guideBookSheet.getSprite("binding", 146, 180)
    override var pageSprite: Sprite = guideBookSheet.getSprite("book", 146, 180)
    override var bannerSprite: Sprite = guideBookSheet.getSprite("banner", 140, 31)
    override var paperSprite: Sprite = guideBookSheet.getSprite("paper", 146, 180)
    override var bookmarkSprite: Sprite = guideBookSheet.getSprite("bookmark", 133, 13)
    override var searchIconSprite: Sprite = guideBookSheet.getSprite("magnifier", 12, 12)
    override var titleBarSprite: Sprite = guideBookSheet.getSprite("title_bar", 86, 11)
    override var nextSpritePressed: Sprite = guideBookSheet.getSprite("arrow_next_pressed", 18, 10)
    override var nextSprite: Sprite = guideBookSheet.getSprite("arrow_next", 18, 10)
    override var backSpritePressed: Sprite = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
    override var backSprite: Sprite = guideBookSheet.getSprite("arrow_back", 18, 10)
    override var homeSpritePressed: Sprite = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
    override var homeSprite: Sprite = guideBookSheet.getSprite("arrow_home", 18, 9)

    var bookmarkID: Int = 0
    override val mainBookComponent: ComponentSprite
    override val paperComponent: ComponentSprite
    override val bindingComponent: ComponentSprite

    override var focus: GuiComponent? = null
    override var history = Stack<IBookElement>()
    override var currentElement: IBookElement? = null

    init {
        this.mainBookComponent = ComponentSprite(pageSprite, 0, 0)
        this.mainBookComponent.color.setValue(book.bookColor)

        paperComponent = ComponentSprite(paperSprite, 0, 0)
        this.mainBookComponent.add(paperComponent)
        bindingComponent = ComponentSprite(bindingSprite, 0, 0)
        bindingComponent.color.setValue(book.bindingColor)
        this.mainBookComponent.add(bindingComponent)

        mainComponents.add(this.mainBookComponent)

        // --------- SEARCH BAR --------- //
        val bar = ComponentSearchBar(this, bookmarkID++,
                TFIDFSearch(this).textBoxConsumer(this) { ComponentSearchResults(this) })
        this.mainBookComponent.add(bar)

        // --------- SEARCH BAR --------- //

        placeInFocus(book)
    }

    override fun makeNavigationButton(offsetIndex: Int, entry: Entry, extra: ((ComponentVoid) -> Unit)?): GuiComponent {
        val indexButton = ComponentVoid(0, 16 * offsetIndex, this.mainBookComponent.size.xi - 32, 16)

        extra?.invoke(indexButton)
        indexButton.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { placeInFocus(entry) }

        // SUB INDEX PLATE RENDERING
        val title = I18n.format(entry.titleKey).replace("&", "§")
        val icon = entry.icon

        val textComponent = ComponentText(20, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        textComponent.unicode.setValue(true)
        textComponent.text.setValue(title)
        indexButton.add(textComponent)

        indexButton.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) { textComponent.text.setValue(" " + TextFormatting.ITALIC.toString() + title) }

        indexButton.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) { textComponent.text.setValue(TextFormatting.RESET.toString() + title) }

        indexButton.render.tooltip.func {
            val list = ArrayList<String>()
            TooltipHelper.addToTooltip(list, entry.titleKey)
            TooltipHelper.addDynamic(list, entry.descKey)

            for (i in 1 until list.size)
                list[i] = TextFormatting.GRAY.toString() + list[i]
            list
        }

        val render = IBookGui.getRendererFor(icon, Vec2d(16.0, 16.0))

        indexButton.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) { render() }

        return indexButton
    }

    override fun updateTextureData(sheet: String, outerColor: Color, bindingColor: Color) {
        val newSheet = ResourceLocation(sheet)
        if (sheetRL != newSheet) {
            sheetRL = newSheet
            guideBookSheet = Texture(ResourceLocation(sheetRL.resourceDomain, "textures/" + sheetRL.resourcePath + ".png"))

            bindingSprite = guideBookSheet.getSprite("binding", 146, 180)
            pageSprite = guideBookSheet.getSprite("book", 146, 180)
            bannerSprite = guideBookSheet.getSprite("banner", 140, 31)
            paperSprite = guideBookSheet.getSprite("paper", 146, 180)
            bookmarkSprite = guideBookSheet.getSprite("bookmark", 133, 13)
            searchIconSprite = guideBookSheet.getSprite("magnifier", 12, 12)
            titleBarSprite = guideBookSheet.getSprite("title_bar", 86, 11)
            nextSpritePressed = guideBookSheet.getSprite("arrow_next_pressed", 18, 10)
            nextSprite = guideBookSheet.getSprite("arrow_next", 18, 10)
            backSpritePressed = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
            backSprite = guideBookSheet.getSprite("arrow_back", 18, 10)
            homeSpritePressed = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
            homeSprite = guideBookSheet.getSprite("arrow_home", 18, 9)
            mainBookComponent.sprite = pageSprite
            paperComponent.sprite = paperSprite
            bindingComponent.sprite = bindingSprite
        }

        mainBookComponent.color.setValue(outerColor)
        bindingComponent.color.setValue(bindingColor)
    }

    companion object {

        var ERROR = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/error.png"))
        var FOF = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/fof.png"))
    }
}
