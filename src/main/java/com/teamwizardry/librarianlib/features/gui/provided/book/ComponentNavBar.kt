package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.util.*

@SideOnly(Side.CLIENT)
class ComponentNavBar(private val book: IBookGui, posX: Int, posY: Int, width: Int, pageCount: Int) : GuiComponent(posX, posY, width, 20) {
    private val nextSprite: Sprite
    var maxPages: Int = 0
    var page = 0
        set(target) {
            val x = MathHelper.clamp(target, 0, maxPages)
            if (this.page == x) return

            field = x

            val eventNavBarChange = EventNavBarChange(this.page)
            BUS.fire(eventNavBarChange)

            val element = book.actualElement()
            if (element != null)
                book.currentElement = ElementWithPage(element, x)
        }

    init {

        maxPages = Math.max(0, pageCount - 1)

        val backSprite = book.backSprite
        val homeSprite = book.homeSprite
        nextSprite = book.nextSprite

        val back = ComponentSprite(backSprite, 0, (size.y / 2.0 - backSprite.height / 2.0).toInt())
        val home = ComponentSprite(homeSprite, (size.x / 2.0 - homeSprite.width / 2.0).toInt(), (size.y / 2.0 - backSprite.height / 2.0).toInt())
        val next = ComponentSprite(nextSprite, (size.x - nextSprite.width).toInt(), (size.y / 2.0 - nextSprite.height / 2.0).toInt())
        add(back, next, home)

        home.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            home.sprite = book.homeSpritePressed
            home.color.setValue(book.book.bookColor.brighter())
        }
        home.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            home.sprite = book.homeSprite
            home.color.setValue(Color.WHITE)
        }
        val homeTooltip = ArrayList<String>()
        homeTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.back"))
        home.render.tooltip.setValue(homeTooltip)

        home.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (GuiScreen.isShiftKeyDown()) {
                book.placeInFocus(book.book)
            } else if (!book.history.empty()) {
                book.forceInFocus(book.history.pop())
            }
        }
        home.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) {
            home.isVisible = !book.history.empty()
        }

        back.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) {
            val x = MathHelper.clamp(this.page - 1, 0, maxPages)
            back.isVisible = this.page != x

            if (back.isVisible) {
                if (it.component.mouseOver) {
                    back.sprite = book.backSpritePressed
                    back.color.setValue(book.book.bookColor.brighter())
                } else {
                    back.sprite = book.backSprite
                    back.color.setValue(Color.WHITE)
                }
            }
        }
        back.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { page -= 1 }
        val backTooltip = ArrayList<String>()
        backTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.previous"))
        back.render.tooltip.setValue(backTooltip)

        next.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) { event ->
            val x = MathHelper.clamp(this.page + 1, 0, maxPages)
            next.isVisible = this.page != x

            if (next.isVisible) {

                if (event.component.mouseOver) {
                    next.color.setValue(book.book.bookColor.brighter())
                    next.sprite = book.nextSpritePressed
                } else {
                    next.color.setValue(Color.WHITE)
                    next.sprite = book.nextSprite
                }
            }
        }
        next.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { page += 1 }
        val nextTooltip = ArrayList<String>()
        nextTooltip.add(I18n.format("${LibrarianLib.MODID}.book.nav.next"))
        next.render.tooltip.setValue(nextTooltip)
    }

    fun whenMaxPagesSet() {
        if (maxPages > 1) {
            val pageStringComponent = ComponentText(size.x.toInt() / 2, (size.y / 2 - nextSprite.height / 2.0).toInt() + 15, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE)
            pageStringComponent.unicode.setValue(false)

            val initialString = (this.page + 1).toString() + "/" + (maxPages + 1)
            pageStringComponent.text.setValue(initialString)

            pageStringComponent.BUS.hook(GuiComponentEvents.ComponentTickEvent::class.java) {
                val pageString = (this.page + 1).toString() + "/" + (maxPages + 1)
                pageStringComponent.text.setValue(pageString)
            }
            add(pageStringComponent)
        }
    }

    class ElementWithPage(private val element: IBookElement, private val page: Int) : IBookElement {

        override val bookParent: IBookElement?
            get() = element.bookParent

        override val heldElement
            get() = element.heldElement

        override fun createComponent(book: IBookGui): GuiComponent {
            val component = element.createComponent(book)
            if (component is NavBarHolder)
                component.navBar.page = page
            return component
        }
    }
}
