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
import java.util.*

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
@Suppress("LeakingThis")
open class ModGuiBook(override val book: Book) : GuiBase(146, 180), IBookGui {
    override val cachedSearchContent = book.contentCache
    private val sheetRL = ResourceLocation(book.textureSheet)
    private val guideBookSheet = Texture(ResourceLocation(sheetRL.resourceDomain, "textures/" + sheetRL.resourcePath + ".png"))

    override val bindingSprite: Sprite = guideBookSheet.getSprite("binding", 146, 180)
    override val pageSprite: Sprite = guideBookSheet.getSprite("book", 146, 180)
    override val bannerSprite: Sprite = guideBookSheet.getSprite("banner", 140, 31)
    override val paperSprite: Sprite = guideBookSheet.getSprite("paper", 146, 180)
    override val bookmarkSprite: Sprite = guideBookSheet.getSprite("bookmark", 133, 13)
    override val searchIconSprite: Sprite = guideBookSheet.getSprite("magnifier", 12, 12)
    override val titleBarSprite: Sprite = guideBookSheet.getSprite("title_bar", 86, 11)
    override val nextSpritePressed: Sprite = guideBookSheet.getSprite("arrow_next_pressed", 18, 10)
    override val nextSprite: Sprite = guideBookSheet.getSprite("arrow_next", 18, 10)
    override val backSpritePressed: Sprite = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
    override val backSprite: Sprite = guideBookSheet.getSprite("arrow_back", 18, 10)
    override val homeSpritePressed: Sprite = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
    override val homeSprite: Sprite = guideBookSheet.getSprite("arrow_home", 18, 9)

    var bookmarkID: Int = 0
    override val mainComponent: ComponentSprite
    override var focus: GuiComponent? = null
    override var history = Stack<IBookElement>()
    override var currentElement: IBookElement? = null

    init {
        this.mainComponent = ComponentSprite(pageSprite, 0, 0)
        this.mainComponent.color.setValue(book.bookColor)

        val bookFilling = ComponentSprite(paperSprite, 0, 0)
        this.mainComponent.add(bookFilling)
        val bookBinding = ComponentSprite(bindingSprite, 0, 0)
        bookBinding.color.setValue(book.bindingColor)
        this.mainComponent.add(bookBinding)

        mainComponents.add(this.mainComponent)

        // --------- SEARCH BAR --------- //
        val bar = ComponentSearchBar(this, bookmarkID++,
                TFIDFSearch(this).textBoxConsumer(this) { ComponentSearchResults(this) })
        this.mainComponent.add(bar)

        // --------- SEARCH BAR --------- //

        placeInFocus(book)
    }

    override fun makeNavigationButton(offsetIndex: Int, entry: Entry, extra: ((ComponentVoid) -> Unit)?): GuiComponent {
        val indexButton = ComponentVoid(0, 16 * offsetIndex, this.mainComponent.size.xi - 32, 16)

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

        indexButton.BUS.hook(GuiComponentEvents.PostDrawEvent::class.java) { render() }

        return indexButton
    }

    companion object {

        var ERROR = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/error.png"))
        var FOF = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/fof.png"))
    }
}
