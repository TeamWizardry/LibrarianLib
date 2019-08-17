package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Align2d
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent

open class HudElementListener(val type: RenderGameOverlayEvent.ElementType?): GuiLayer() {
    open fun hudEvent(e: RenderGameOverlayEvent.Post) {

    }

    open fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        this.isVisible = true
    }

}

open class HudElement(type: RenderGameOverlayEvent.ElementType? = null): HudElementListener(type) {
    protected val mc: Minecraft get() = Minecraft.getMinecraft()

    val left: StackLayer = StackLayout.build()
        .horizontal().reverse()
        .align(Align2d.CENTER_RIGHT)
        .layer()

    val right: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.CENTER_LEFT)
        .layer()

    val top: StackLayer = StackLayout.build()
        .vertical().reverse()
        .align(Align2d.BOTTOM_CENTER)
        .layer()

    val bottom: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_CENTER)
        .layer()

    protected var autoContentBounds = AutoContentBounds.FULL_FRAME
    protected enum class AutoContentBounds {
        NONE, FULL_FRAME, CONTENTS;
    }

    init {
        left.addTag(StackTag)
        right.addTag(StackTag)
        top.addTag(StackTag)
        bottom.addTag(StackTag)
        this.add(left, right, top, bottom)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        left.fitToLength()
        right.fitToLength()
        top.fitToLength()
        bottom.fitToLength()

        val contentBounds = when(autoContentBounds) {
            AutoContentBounds.NONE -> null
            AutoContentBounds.FULL_FRAME -> this.bounds
            AutoContentBounds.CONTENTS -> this.getContentsBounds {
                it.isVisible &&
                    it != this && !it.hasTag(StackTag) &&
                    !excludeFromContentBounds(it)
            }
        }

        if(contentBounds != null) {
            left.frame = rect(
                contentBounds.minX-left.width, contentBounds.minY,
                left.width, contentBounds.height
            )
            right.frame = rect(
                contentBounds.maxX, contentBounds.minY,
                right.width, contentBounds.height
            )
            top.frame = rect(
                contentBounds.minX-top.height, contentBounds.minY,
                contentBounds.width, top.height
            )
            bottom.frame = rect(
                contentBounds.minX, contentBounds.maxY,
                contentBounds.width, bottom.height
            )
        }
    }

    protected open fun excludeFromContentBounds(layer: GuiLayer): Boolean = false

    private companion object StackTag
}