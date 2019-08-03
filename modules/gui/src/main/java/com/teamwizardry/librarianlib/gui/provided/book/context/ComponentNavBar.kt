package com.teamwizardry.librarianlib.gui.provided.book.context

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.gui.components.ComponentText
import com.teamwizardry.librarianlib.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.util.*

@SideOnly(Side.CLIENT)
class ComponentNavBar(private val book: IBookGui, posX: Int, posY: Int, width: Int) : GuiComponent(posX, posY, width, 20) {
    private val nextSprite: Sprite
    val maxPage: Int
        get() = book.context.pages.size - 1

    var page: Int
        get() = book.context.position
        set(target) {
            val x = MathHelper.clamp(target, 0, maxPage)
            if (this.page == x) return

            val eventNavBarChange = EventNavBarChange(this.page)
            BUS.fire(eventNavBarChange)

            book.changePage(x)
        }

    init {

        BUS.hook(GuiLayerEvents.Tick::class.java) {
            isVisible = maxPage > 0 || book.context.parent != null
        }

        val backSprite = book.backSprite
        val homeSprite = book.homeSprite
        nextSprite = book.nextSprite

        val back = ComponentSprite(backSprite, 0, (size.y / 2.0 - backSprite.height / 2.0).toInt())
        val home = ComponentSprite(homeSprite, (size.x / 2.0 - homeSprite.width / 2.0).toInt(), (size.y / 2.0 - backSprite.height / 2.0).toInt())
        val next = ComponentSprite(nextSprite, (size.x - nextSprite.width).toInt(), (size.y / 2.0 - nextSprite.height / 2.0).toInt())
        add(back, next, home)

        home.BUS.hook(GuiComponentEvents.MouseMoveInEvent::class.java) {
            home.sprite = book.homeSpritePressed
            home.color = book.book.bookColor.brighter()
        }
        home.BUS.hook(GuiComponentEvents.MouseMoveOutEvent::class.java) {
            home.sprite = book.homeSprite
            home.color = Color.WHITE
        }
        val homeTooltip = ArrayList<String>()
        homeTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.back"))
//        home.tooltip = homeTooltip

        home.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (GuiScreen.isShiftKeyDown()) {
                book.focusOn(BookContext(book, book.book.createComponents(book),
                        book.book, book.book.addAllBookmarks(null), book.context))
            } else
                book.up()
        }
        home.BUS.hook(GuiLayerEvents.Tick::class.java) {
            home.isVisible = book.context.parent != null
        }

        back.BUS.hook(GuiLayerEvents.Tick::class.java) {
            val x = MathHelper.clamp(this.page - 1, 0, maxPage)
            back.isVisible = this.page != x

            if (back.isVisible) {
                if (back.mouseOver) {
                    back.sprite = book.backSpritePressed
                    back.color = book.book.bookColor.brighter()
                } else {
                    back.sprite = book.backSprite
                    back.color = Color.WHITE
                }
            }
        }
        back.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (GuiScreen.isShiftKeyDown())
                page = 0
            else
                page--
        }
        val backTooltip = ArrayList<String>()
        backTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.previous"))
//        back.tooltip = backTooltip

        next.BUS.hook(GuiLayerEvents.Tick::class.java) { event ->
            val x = MathHelper.clamp(this.page + 1, 0, maxPage)
            next.isVisible = this.page != x

            if (next.isVisible) {

                if (next.mouseOver) {
                    next.color = book.book.bookColor.brighter()
                    next.sprite = book.nextSpritePressed
                } else {
                    next.color = Color.WHITE
                    next.sprite = book.nextSprite
                }
            }
        }
        next.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (GuiScreen.isShiftKeyDown())
                page = maxPage
            else
                page++
        }
        val nextTooltip = ArrayList<String>()
        nextTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.next"))
//        next.tooltip = nextTooltip

        val pageStringComponent = ComponentText(size.x.toInt() / 2, (size.y / 2 - nextSprite.height / 2.0).toInt() + 15, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE)
        pageStringComponent.unicode = false

        pageStringComponent.text_im { "${this.page + 1}/${maxPage + 1}" }
        pageStringComponent.BUS.hook(GuiLayerEvents.Tick::class.java) {
            pageStringComponent.isVisible = this.maxPage > 0
        }
        add(pageStringComponent)
    }
}
