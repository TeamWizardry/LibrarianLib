package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.context.BookContext
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentNavBar
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import java.awt.Color

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
@Suppress("LeakingThis")
open class ModGuiBook(override val book: Book) : GuiBase(146, 180), IBookGui {
    override val cachedSearchContent = book.contentCache
    private var sheetRL = ResourceLocation(book.textureSheet)
    private var guideBookSheet = Texture(ResourceLocation(sheetRL.namespace, "textures/" + sheetRL.path + ".png"))

    override var bindingSprite: Sprite = guideBookSheet.getSprite("binding", 146, 180)
    override var pageSprite: Sprite = guideBookSheet.getSprite("book", 146, 180)
    override var bannerSprite: Sprite = guideBookSheet.getSprite("banner", 140, 31)
    override var paperSprite: Sprite = guideBookSheet.getSprite("paper", 146, 180)
    override var bookmarkSprite: Sprite = guideBookSheet.getSprite("bookmark", 133, 13)
    override var searchIconSprite: Sprite = guideBookSheet.getSprite("magnifier", 12, 12)
    override var titleBarSprite: Sprite = guideBookSheet.getSprite("title_bar", 115, 11)
    override var nextSpritePressed: Sprite = guideBookSheet.getSprite("arrow_next_pressed", 18, 10)
    override var nextSprite: Sprite = guideBookSheet.getSprite("arrow_next", 18, 10)
    override var backSpritePressed: Sprite = guideBookSheet.getSprite("arrow_back_pressed", 18, 9)
    override var backSprite: Sprite = guideBookSheet.getSprite("arrow_back", 18, 10)
    override var homeSpritePressed: Sprite = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
    override var homeSprite: Sprite = guideBookSheet.getSprite("arrow_home", 18, 9)
    override var processArrow: Sprite = guideBookSheet.getSprite("process_arrow", 18, 9)
    override var materialIcon: Sprite = guideBookSheet.getSprite("material_icon", 16, 16)

    override val mainBookComponent: ComponentSprite
    override val paperComponent: ComponentSprite
    override val bindingComponent: ComponentSprite

    override var focus: GuiComponent? = null

    override val navBar: ComponentNavBar


    init {
        mainBookComponent = ComponentSprite(pageSprite, 0, 0)
        mainBookComponent.color = book.bookColor

        paperComponent = ComponentSprite(paperSprite, 0, 0)
        mainBookComponent.add(paperComponent)
        bindingComponent = ComponentSprite(bindingSprite, 0, 0)
        bindingComponent.color = book.bindingColor
        mainBookComponent.add(bindingComponent)

        navBar = ComponentNavBar(this, mainBookComponent.size.xi / 2 - 35, mainBookComponent.size.yi, 70)
        mainBookComponent.add(navBar)

        mainComponents.add(this.mainBookComponent)
    }

    override var context: BookContext = focusOn(BookContext(this, book))

    override fun makeNavigationButton(offsetIndex: Int, entry: Entry, extra: ((ComponentVoid) -> Unit)?): GuiComponent {
        val indexButton = ComponentVoid(0, 16 * offsetIndex, this.mainBookComponent.size.xi - 32, 16)

        extra?.invoke(indexButton)
        indexButton.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { placeInFocus(entry) }

        // SUB INDEX PLATE RENDERING
        val title = entry.title.toString()
        val icon = entry.icon

        val textComponent = ComponentText(20, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        textComponent.unicode = true
        textComponent.text = title
        indexButton.add(textComponent)

        indexButton.BUS.hook(GuiLayerEvents.MouseInEvent::class.java) { textComponent.text = " " + TextFormatting.ITALIC.toString() + title }

        indexButton.BUS.hook(GuiLayerEvents.MouseOutEvent::class.java) { textComponent.text = TextFormatting.RESET.toString() + title }

        indexButton.tooltip_im {
            val list = mutableListOf<String>()
            entry.title?.add(list)
            entry.desc?.addDynamic(list)

            for (i in 1 until list.size)
                list[i] = TextFormatting.GRAY.toString() + list[i]
            list
        }

        val render = IBookGui.getRendererFor(icon, Vec2d(16.0, 16.0))

        indexButton.BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) { render() }

        return indexButton
    }

    override fun updateTextureData(sheet: String, outerColor: Color, bindingColor: Color) {
        val newSheet = ResourceLocation(sheet)
        if (sheetRL != newSheet) {
            sheetRL = newSheet
            guideBookSheet = Texture(ResourceLocation(sheetRL.namespace, "textures/" + sheetRL.path + ".png"))

            bindingSprite = guideBookSheet.getSprite("binding", 146, 180)
            pageSprite = guideBookSheet.getSprite("book", 146, 180)
            bannerSprite = guideBookSheet.getSprite("banner", 140, 31)
            paperSprite = guideBookSheet.getSprite("paper", 146, 180)
            bookmarkSprite = guideBookSheet.getSprite("bookmark", 133, 13)
            searchIconSprite = guideBookSheet.getSprite("magnifier", 12, 12)
            titleBarSprite = guideBookSheet.getSprite("title_bar", 115, 11)
            nextSpritePressed = guideBookSheet.getSprite("arrow_next_pressed", 18, 10)
            nextSprite = guideBookSheet.getSprite("arrow_next", 18, 10)
            backSpritePressed = guideBookSheet.getSprite("arrow_back_pressed", 18, 9)
            backSprite = guideBookSheet.getSprite("arrow_back", 18, 10)
            homeSpritePressed = guideBookSheet.getSprite("arrow_home_pressed", 18, 9)
            homeSprite = guideBookSheet.getSprite("arrow_home", 18, 9)
            processArrow = guideBookSheet.getSprite("process_arrow", 18, 9)
            materialIcon = guideBookSheet.getSprite("material_icon", 16, 16)
            mainBookComponent.sprite = pageSprite
            paperComponent.sprite = paperSprite
            bindingComponent.sprite = bindingSprite
        }

        mainBookComponent.color = outerColor
        bindingComponent.color = bindingColor
    }

    companion object {

        var ERROR = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/error.png"))
        var FOF = Sprite(ResourceLocation(LibrarianLib.MODID, "textures/gui/book/error/fof.png"))
    }
}
