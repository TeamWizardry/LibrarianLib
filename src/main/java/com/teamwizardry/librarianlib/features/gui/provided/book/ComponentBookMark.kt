package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.sprite.Sprite
import java.awt.Color
import java.awt.SystemColor.text

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
open class ComponentBookMark(val book: IBookGui, val icon: Sprite, val id: Int, iconExtraX: Int = 0, iconExtraY: Int = 0) : ComponentAnimatableVoid(book.mainBookComponent.size.xi - 10,
        20 + 5 * id + book.bookmarkSprite.height * id, book.bookmarkSprite.width, book.bookmarkSprite.height) {
    private val box: Sprite = book.bookmarkSprite

    /**
     * This is made public so that you can add whatever shit you want to it directly.
     * By adding shit to this specifically, your shit will animate properly with it.
     */
    val bar: ComponentSprite

    init {
        clipping.clipToBounds = true

        animX = (-box.width + 20).toDouble()

        bar = ComponentSprite(book.bookmarkSprite, -box.width + 20, 0)
        bar.color.setValue(book.book.bookColor)
        add(bar)

        val iconComponent = ComponentSprite(icon, size.xi - icon.width + iconExtraX, iconExtraY)
        bar.add(iconComponent)
    }

    fun setBookmarkText(textString: String = "", textColor: Color = Color.WHITE, extraX: Int = 0) {
        val textComp = ComponentText(size.xi - icon.width + extraX, 2, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP)
        textComp.text.setValue(textString)
        textComp.color.setValue(textColor)
        textComp.transform.translateZ = 10.0
        textComp.unicode.setValue(false)
        bar.add(textComp)
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
