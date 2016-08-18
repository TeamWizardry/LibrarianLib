package com.teamwizardry.librarianlib.book.gui

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.util.Page
import com.teamwizardry.librarianlib.data.DataNode
import com.teamwizardry.librarianlib.gui.GuiBase
import com.teamwizardry.librarianlib.gui.components.ComponentSliderTray
import com.teamwizardry.librarianlib.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.gui.components.ComponentText
import com.teamwizardry.librarianlib.gui.components.ComponentText.TextAlignH
import com.teamwizardry.librarianlib.gui.components.ComponentText.TextAlignV
import com.teamwizardry.librarianlib.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin
import com.teamwizardry.librarianlib.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.gui.template.SliderTemplate
import com.teamwizardry.librarianlib.sprite.Texture
import com.teamwizardry.librarianlib.util.Color
import com.teamwizardry.librarianlib.util.PathUtils
import net.minecraft.util.ResourceLocation
import java.util.*

open class GuiBook(val book: Book, protected val rootData: DataNode, protected val pageData: DataNode, val page: Page) : GuiBase(146, 180) {
    protected var contents: ComponentVoid
    protected var tips: ComponentVoid

    init {

        // title bar
        val titleBar = ComponentVoid((BACKGROUND_PAGE.width - TITLE_BAR.width) / 2, -19, TITLE_BAR.width, TITLE_BAR.height)
        titleBar.add(ComponentSprite(TITLE_BAR, 0, 0))
        titleBar.add(ComponentText(66, 7, TextAlignH.CENTER, TextAlignV.MIDDLE).`val`("TITLE"))

        // nav
        val navBar = ComponentVoid((BACKGROUND_PAGE.width - TITLE_BAR.width) / 2, 186, TITLE_BAR.width, TITLE_BAR.height)

        val disabledColor = Color.rgb(0xB0B0B0)
        val hoverColor = Color.rgb(0x0DDED3)
        val normalColor = Color.rgb(0x0DBFA2)

        navBar.add(ComponentSprite(TITLE_BAR, 0, 0))

        // back page
        val backPageButton = ComponentSprite(BACK_PAGE, 15, 2)
        ButtonMixin(backPageButton) { backPageButton.color.setValue(normalColor) }

        backPageButton.enabled = pageData.get("hasPrev").exists()
        backPageButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            openPage(this.page.path, this.page.page - 1)
        }
        backPageButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when(event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> backPageButton.color.setValue(normalColor)
                ButtonMixin.EnumButtonState.DISABLED -> backPageButton.color.setValue(disabledColor)
                ButtonMixin.EnumButtonState.HOVER -> backPageButton.color.setValue(hoverColor)
            }
        }

        navBar.add(backPageButton)

        // next page
        val nextPageButton = ComponentSprite(NEXT_PAGE, TITLE_BAR.width - NEXT_PAGE.width - 15, 2)
        ButtonMixin(nextPageButton) { nextPageButton.color.setValue(normalColor) }

        nextPageButton.enabled = pageData.get("hasNext").exists()
        nextPageButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            openPage(this.page.path, this.page.page + 1)
        }
        nextPageButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when(event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> nextPageButton.color.setValue(normalColor)
                ButtonMixin.EnumButtonState.DISABLED -> nextPageButton.color.setValue(disabledColor)
                ButtonMixin.EnumButtonState.HOVER -> nextPageButton.color.setValue(hoverColor)
            }
        }
        navBar.add(nextPageButton)

        // back arrow
        val backArrowButton = ComponentSprite(BACK_ARROW, TITLE_BAR.width / 2 - BACK_ARROW.width / 2, 2)
        ButtonMixin(backArrowButton) { backArrowButton.color.setValue(normalColor) }

        backArrowButton.enabled = book.history.size > 0
        backArrowButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            book.back()
        }
        backArrowButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when(event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> backArrowButton.color.setValue(normalColor)
                ButtonMixin.EnumButtonState.DISABLED -> backArrowButton.color.setValue(disabledColor)
                ButtonMixin.EnumButtonState.HOVER -> backArrowButton.color.setValue(hoverColor)
            }
        }
        navBar.add(backArrowButton)

        // page
        val contents = ComponentVoid(13, 9, PAGE_WIDTH, PAGE_HEIGHT)
        ScissorMixin.scissor(contents)

        // bg/fg
        val border = ComponentSprite(BOOK_BACKGROUND_BORDER, 0, 0)
        border.depth.setValue(false)

        val pageBG = ComponentSprite(BACKGROUND_PAGE, 0, 0)

        tips = ComponentVoid(0, 0, 0, PAGE_HEIGHT)

        tips.zIndex = -100
        pageBG.zIndex = -5
        contents.zIndex = 0
        titleBar.zIndex = 9
        navBar.zIndex = 9
        border.zIndex = -10

        components.add(tips)
        components.add(pageBG)
        components.add(border)
        components.add(titleBar)
        if (book.history.size > 0 || pageData.get("hasNext").exists() || pageData.get("hasPrev").exists())
            components.add(navBar)
        components.add(contents)

        this.contents = contents
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    fun openPageRelative(path: String, page: Int) {
        openPage(PathUtils.resolve(PathUtils.parent(this.page.path), path), page)
    }

    fun openPage(path: String, page: Int) {
        book.display(Page(path, page))
    }

    fun pageResource(path: String): ResourceLocation {
        return ResourceLocation(book.modid, PathUtils.resolve("textures/" + PathUtils.resolve(PathUtils.parent(this.page.path), path)))
    }

    private val sliders = WeakHashMap<Any, ComponentSliderTray>()

    fun addTextSlider(key: Any, y: Int, text: String) {
        removeSlider(key)

        val slider = SliderTemplate.text(y, text)
        sliders.put(key, slider)

        tips.add(slider)
    }

    fun removeSlider(key: Any) {
        if (sliders.containsKey(key))
            sliders[key]?.invalidate()
    }

    companion object {

        // TODO: LibSprites for liblib
        val PAGE_WIDTH = 120
        val PAGE_HEIGHT = 161
        var TEXTURE = Texture(ResourceLocation(LibrarianLib.MODID, "textures/book/book.png"))
        var BOOK_BACKGROUND_BORDER = TEXTURE.getSprite("background_border", 146, 180)
        var BACKGROUND_PAGE = TEXTURE.getSprite("background_page", 146, 180)
        var TITLE_BAR = TEXTURE.getSprite("title_bar", 133, 13)
        var BOOKMARK = TEXTURE.getSprite("bookmark", 100, 13)
        var BACK_PAGE = TEXTURE.getSprite("back_page", 18, 10)
        var NEXT_PAGE = TEXTURE.getSprite("next_page", 18, 10)
        var BACK_ARROW = TEXTURE.getSprite("back_arrow", 18, 9)
        var UP_ARROW = TEXTURE.getSprite("up_arrow", 9, 18)
        var DOWN_ARROW = TEXTURE.getSprite("down_arrow", 9, 18)
        var CHECKBOX = TEXTURE.getSprite("checkbox", 9, 9)
        var CHECKBOX_ON = TEXTURE.getSprite("checkbox_on", 9, 9)
        var CHECKMARK = TEXTURE.getSprite("checkmark", 16, 16)
        var SLIDER_NORMAL = TEXTURE.getSprite("slider_normal", 133, 37)
        var SLIDER_RECIPE = TEXTURE.getSprite("slider_recipe", 133, 68)
        var BUTTON = TEXTURE.getSprite("button", 32, 32)
    }
}
