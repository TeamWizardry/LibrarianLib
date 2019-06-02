package com.teamwizardry.librarianlib.features.facade.provided.book.context

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.facade.components.ComponentAnimatableVoid
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.Minecraft
import java.awt.Color

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
open class ComponentBookMark(val book: IBookGui, val icon: Sprite, val id: Int, iconExtraX: Int = 0, iconExtraY: Int = 0) : ComponentAnimatableVoid(
        book.mainBookComponent.size.xi - 16,
        5 * id + book.bookmarkSprite.height * id,
        book.bookmarkSprite.width, book.bookmarkSprite.height) {
    private val box: Sprite = book.bookmarkSprite

    /**
     * This is made public so that you can add whatever shit you want to it directly.
     * By adding shit to this specifically, your shit will animate properly with it.
     */
    val bar: ComponentSprite

    init {
        clipToBounds = true

        animX = (-box.width + 20).toDouble()

        bar = ComponentSprite(book.bookmarkSprite, -box.width + 20, 0)
        bar.color = book.book.bookColor
        add(bar)

        val iconComponent = ComponentSprite(icon, size.xi - icon.width + iconExtraX, iconExtraY)
        bar.add(iconComponent)
    }

    fun setBookmarkText(textString: String = "", textColor: Color = Color.WHITE, extraX: Int = 0) {
        val textComp = ComponentText(size.xi - icon.width + extraX, 2, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP)
        pad = 10 + Minecraft.getMinecraft().fontRenderer.getStringWidth(textString)
        textComp.text = textString
        textComp.color = textColor
        textComp.translateZ = 10.0
        textComp.unicode = false
        textComp.shadow = true
        bar.add(textComp)
        slideIn()
    }

    fun slideBy(amount: Int) {
        val mouseOutAnim = BasicAnimation(bar, "pos.x")
        mouseOutAnim.duration = 10f
        mouseOutAnim.easing = Easing.easeOutQuart
        mouseOutAnim.to = amount - slideWidth
        bar.add(mouseOutAnim)
    }

    private val slideWidth = bar.size.xi - 20

    private var pad = 0

    open fun slideOutShort() = slideBy((slideWidth - pad) / 2 + pad)
    open fun slideOutLong() = slideBy(slideWidth)
    open fun slideIn() = slideBy(pad)
}
