package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.sprite.Sprite

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
open class ComponentBookMark(val book: IBookGui, icon: Sprite, val id: Int) : ComponentAnimatableVoid(book.mainComponent.size.xi - 10,
        20 + 5 * id + book.bookmarkSprite.height * id, book.bookmarkSprite.width, book.bookmarkSprite.height) {
    private val box: Sprite = book.bookmarkSprite

    private val bar: ComponentSprite

    init {
        clipping.clipToBounds = true

        animX = (-box.width + 20).toDouble()

        bar = ComponentSprite(book.bookmarkSprite, -box.width + 20, 0)
        bar.color.setValue(book.book.bookColor)
        add(bar)

        val iconComponent = ComponentSprite(icon, size.xi - icon.width - 8, 1)
        bar.add(iconComponent)
    }

    fun slideOutShort() {
        val mouseOutAnim = BasicAnimation(bar, "pos.x")
        mouseOutAnim.duration = 10f
        mouseOutAnim.easing = Easing.easeOutQuart
        mouseOutAnim.to = -40
        bar.add(mouseOutAnim)
    }

    fun slideOutLong() {
        val mouseOutAnim = BasicAnimation(bar, "pos.x")
        mouseOutAnim.duration = 10f
        mouseOutAnim.easing = Easing.easeOutQuart
        mouseOutAnim.to = 0
        bar.add(mouseOutAnim)
    }

    fun slideIn() {
        val mouseOutAnim = BasicAnimation(bar, "pos.x")
        mouseOutAnim.duration = 10f
        mouseOutAnim.easing = Easing.easeOutQuart
        mouseOutAnim.to = -box.width + 20
        bar.add(mouseOutAnim)
    }
}
