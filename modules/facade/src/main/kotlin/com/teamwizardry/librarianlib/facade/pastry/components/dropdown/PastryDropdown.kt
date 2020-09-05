package com.teamwizardry.librarianlib.facade.pastry.components.dropdown

import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.Pastry
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.facade.pastry.components.PastryActivatedControl
import com.teamwizardry.librarianlib.math.ScreenSpace
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec
import java.util.function.Consumer

public class PastryDropdown<T> constructor(
    posX: Int, posY: Int,
    width: Int,
    callback: Consumer<T>?
) : PastryActivatedControl(posX, posY, width, Pastry.lineHeight) {

    public val items: MutableList<PastryDropdownItem<T>> = mutableListOf()
    public var selected: PastryDropdownItem<T>? = null
        private set

    private val sprite = SpriteLayer(PastryTexture.dropdown, 0, 0, widthi, heighti)
    private var menu: DropdownMenu<T>? = null
    internal var buttonContents: GuiLayer? = null

    init {
        if(callback != null)
            this.BUS.hook<SelectEvent<T>> {
                callback.accept(it.value)
            }
        this.add(sprite)
    }

    public fun selectIndex(index: Int) {
        val newItem = items[index]
        if(selected === newItem) return
        if(newItem.decoration) {
            selected = null
            buttonContents?.also { remove(it) }
            buttonContents = null
        } else {
            selected = newItem
            buttonContents?.also { remove(it) }
            buttonContents = newItem.createLayer().also { add(it) }
        }
        if(!newItem.decoration)
            BUS.fire(SelectEvent(newItem.value))
    }

    public fun select(value: T) {
        val index = items.indexOfFirst {
            !it.decoration && it.value == value
        }
        if(index == -1)
            throw IllegalArgumentException("Could not find dropdown item with value of $value")
        selectIndex(index)
    }

    private fun openMenu(mouseActivated: Boolean) {
        val menu = DropdownMenu(this, mouseActivated)
        this.menu = menu
        root.add(menu)

        menu.initialMousePos = convertPointTo(mousePos, ScreenSpace)
        menu.pos = this.convertPointTo(vec(0, 0), root)
        selected?.also { menu.scrollTo(it) }
    }

    internal fun menuClosed() {

    }

    override fun activate() {
        openMenu(false)
    }

    @Hook
    private fun removeFromParent(e: GuiLayerEvents.RemoveFromParentEvent) {
        menu?.also {
            it.parent?.remove(it)
        }
    }

    @Hook
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if(this.mouseOver) {
            openMenu(true)
        }
    }

    override fun layoutChildren() {
        sprite.size = this.size
        buttonContents?.frame = rect(2, 2, width - 10, height - 4)
    }

    public fun sizeToFit() {
        this.width = items.map { it.createLayer().width + 15 }.maxOrNull() ?: this.width
    }

    public class SelectEvent<T>(public val value: T): CancelableEvent()
}