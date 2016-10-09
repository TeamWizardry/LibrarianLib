package com.teamwizardry.librarianlib.client.book.gui

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.bloat.PathUtils
import com.teamwizardry.librarianlib.client.book.util.BookRegistry
import com.teamwizardry.librarianlib.client.book.util.BookSection
import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentSliderTray
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.client.gui.components.ComponentText
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin
import com.teamwizardry.librarianlib.client.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.client.gui.template.SliderTemplate
import com.teamwizardry.librarianlib.client.sprite.Texture
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*

open class GuiBook(val section: BookSection) : GuiBase(146, 180) {
    protected var contents: ComponentVoid
    protected var tips: ComponentVoid
    private var dontSaveToHistory = false

    private val backPageButton: GuiComponent<*>
    private val nextPageButton: GuiComponent<*>
    private val backArrowButton: GuiComponent<*>
    private val navBar: GuiComponent<*>

    init {

        // title bar
        val titleBar = ComponentVoid((BACKGROUND_PAGE.width - TITLE_BAR.width) / 2, -19, TITLE_BAR.width, TITLE_BAR.height)
        titleBar.add(ComponentSprite(TITLE_BAR, 0, 0))
        titleBar.add(ComponentText(66, 7, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE).`val`("TITLE"))

        val reload = ComponentSprite(RELOAD, BACKGROUND_PAGE.width - 8, -8)

        ButtonMixin(reload) { reload.color.setValue(Color.RED) }
        reload.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when (event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> reload.color.setValue(Color.RED)
                ButtonMixin.EnumButtonState.HOVER -> reload.color.setValue(Color.GREEN)
                ButtonMixin.EnumButtonState.DISABLED -> reload.color.setValue(Color.WHITE)
            }
        }
        reload.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) { event ->
            BookRegistry.clearCache()
            section.entry.book.history.clear()
            dontSaveToHistory = true
        }


        // nav
        navBar = ComponentVoid((BACKGROUND_PAGE.width - TITLE_BAR.width) / 2, 186, TITLE_BAR.width, TITLE_BAR.height)

        val disabledColor = Color(0xB0B0B0)
        val hoverColor = Color(0x0DDED3)
        val normalColor = Color(0x0DBFA2)

        navBar.add(ComponentSprite(TITLE_BAR, 0, 0))

        // back page
        backPageButton = ComponentSprite(BACK_PAGE, 15, 2)
        ButtonMixin(backPageButton) { backPageButton.color.setValue(normalColor) }

        backPageButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            prevPage()
        }
        backPageButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when (event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> backPageButton.color.setValue(normalColor)
                ButtonMixin.EnumButtonState.DISABLED -> backPageButton.color.setValue(disabledColor)
                ButtonMixin.EnumButtonState.HOVER -> backPageButton.color.setValue(hoverColor)
            }
        }

        navBar.add(backPageButton)

        // next page
        nextPageButton = ComponentSprite(NEXT_PAGE, TITLE_BAR.width - NEXT_PAGE.width - 15, 2)
        ButtonMixin(nextPageButton) { nextPageButton.color.setValue(normalColor) }

        nextPageButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            nextPage()
        }
        nextPageButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when (event.newState) {
                ButtonMixin.EnumButtonState.NORMAL -> nextPageButton.color.setValue(normalColor)
                ButtonMixin.EnumButtonState.DISABLED -> nextPageButton.color.setValue(disabledColor)
                ButtonMixin.EnumButtonState.HOVER -> nextPageButton.color.setValue(hoverColor)
            }
        }
        navBar.add(nextPageButton)

        // back arrow
        backArrowButton = ComponentSprite(BACK_ARROW, TITLE_BAR.width / 2 - BACK_ARROW.width / 2, 2)
        ButtonMixin(backArrowButton) { backArrowButton.color.setValue(normalColor) }

        backArrowButton.BUS.hook(ButtonMixin.ButtonClickEvent::class.java) {
            section.entry.book.back()
        }
        backArrowButton.BUS.hook(ButtonMixin.ButtonStateChangeEvent::class.java) { event ->
            when (event.newState) {
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
        border.zIndex = 10
        reload.zIndex = 11

        mainComponents.add(tips)
        mainComponents.add(pageBG)
        mainComponents.add(border)
        mainComponents.add(titleBar)
        mainComponents.add(navBar)
        mainComponents.add(contents)
        mainComponents.add(reload)

        this.contents = contents


    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        nextPageButton.enabled = section.nextSection != null || hasNextPage()
        backPageButton.enabled = section.prevSection != null || hasPrevPage()

        backArrowButton.isVisible = section.entry.book.history.size > 0

        navBar.isVisible = nextPageButton.enabled || backPageButton.enabled || backArrowButton.isVisible

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    open fun jumpToPage(page: Int) {
        //NO-OP
    }

    open fun pageJump(): Int = 0
    open fun maxpPageJump(): Int = 0

    open fun hasNextPage(): Boolean = false
    open fun hasPrevPage(): Boolean = false

    open fun goToNextPage() {
        //NO-OP
    }

    open fun goToPrevPage() {
        //NO-OP
    }

    fun nextPage() {
        val sec = section.nextSection
        if (hasNextPage())
            goToNextPage()
        else if (sec != null)
            openPage(sec.entry.path, sec.sectionTag)
    }

    fun prevPage() {
        val sec = section.prevSection
        if (hasPrevPage())
            goToPrevPage()
        else if (sec != null)
            openPage(sec.entry.path, sec.sectionTagEnd)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        if (!dontSaveToHistory)
            section.entry.book.pushHistory(section, pageJump()).gui = this
    }

    fun openPageRelative(path: String, tag: String) {
        openPage(PathUtils.resolve(PathUtils.parent(section.entry.path), path), tag)
    }

    fun openPage(path: String, tag: String) {
        section.entry.book.display(path, tag)
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
        var RELOAD = TEXTURE.getSprite("reload", 16, 16)
    }
}
