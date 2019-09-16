package com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown

import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.component.LayerHierarchyException
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.Pastry
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryActivatedControl
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

class PastryDropdown<T> constructor(
    posX: Int, posY: Int,
    width: Int,
    callback: ((T) -> Unit)?
) : PastryActivatedControl(posX, posY, width, Pastry.lineHeight) {

    val items = mutableListOf<PastryDropdownItem<T>>()
    var selected: PastryDropdownItem<T>? = null
        private set

    private val sprite = SpriteLayer(PastryTexture.dropdown, 0, 0, widthi, heighti)
    private var menu: DropdownMenu<T>? = null
    internal var buttonContents: GuiLayer? = null

    init {
        if(callback != null)
            this.BUS.hook<SelectEvent<T>> {
                callback(it.value)
            }
        this.cursor = LibCursor.POINT
        this.add(sprite)
    }

    fun selectIndex(index: Int) {
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

    fun select(value: T) {
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
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(this.mouseOver) {
            openMenu(true)
        }
    }

    override fun layoutChildren() {
        sprite.size = this.size
        buttonContents?.frame = rect(2, 2, width - 10, height - 4)
    }

    fun sizeToFit() {
        this.width = items.map { it.createLayer().width + 15 }.max() ?: this.width
    }

    class SelectEvent<T>(val value: T): EventCancelable()
}