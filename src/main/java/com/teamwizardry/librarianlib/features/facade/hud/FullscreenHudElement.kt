package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.layout.StackLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import net.minecraftforge.client.event.RenderGameOverlayEvent

class FullscreenHudElement(type: RenderGameOverlayEvent.ElementType): HudElementListener(type) {
    val hLeft: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.CENTER_LEFT)
        .layer()

    val vLeft: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_LEFT)
        .layer()

    val hRight: StackLayer = StackLayout.build()
        .horizontal().reverse()
        .align(Align2d.CENTER_RIGHT)
        .layer()

    val vRight: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_RIGHT)
        .layer()

    val vTop: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_CENTER)
        .layer()

    val hTop: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.TOP_LEFT)
        .layer()

    val vBottom: StackLayer = StackLayout.build()
        .vertical().reverse()
        .align(Align2d.BOTTOM_CENTER)
        .layer()

    val hBottom: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.BOTTOM_LEFT)
        .layer()

    val vTopLeft: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_LEFT)
        .layer()

    val hTopLeft: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.TOP_LEFT)
        .layer()

    val vTopRight: StackLayer = StackLayout.build()
        .vertical()
        .align(Align2d.TOP_RIGHT)
        .layer()

    val hTopRight: StackLayer = StackLayout.build()
        .horizontal().reverse()
        .align(Align2d.TOP_RIGHT)
        .layer()

    val vBottomLeft: StackLayer = StackLayout.build()
        .vertical().reverse()
        .align(Align2d.BOTTOM_LEFT)
        .layer()

    val hBottomLeft: StackLayer = StackLayout.build()
        .horizontal()
        .align(Align2d.BOTTOM_LEFT)
        .layer()

    val vBottomRight: StackLayer = StackLayout.build()
        .vertical().reverse()
        .align(Align2d.BOTTOM_RIGHT)
        .layer()

    val hBottomRight: StackLayer = StackLayout.build()
        .horizontal().reverse()
        .align(Align2d.BOTTOM_RIGHT)
        .layer()

    init {
        this.forEachChild {
            this.remove(it)
        }
        this.add(
            vTopLeft, vTop, vTopRight,
            hLeft, hRight,
            vBottomLeft, vBottom, vBottomRight
        )

        vTopLeft.add(hTopLeft)
        vTopRight.add(hTopRight)
        vBottomLeft.add(hBottomLeft)
        vBottomRight.add(hBottomRight)
        vTop.add(hTop)
        vBottom.add(hBottom)
        hLeft.add(vLeft)
        hRight.add(vRight)

        vTopLeft.anchor = vec(0, 0)
        vTopRight.anchor = vec(1, 0)
        vBottomLeft.anchor = vec(0, 1)
        vBottomRight.anchor = vec(1, 1)

        vTop.anchor = vec(0.5, 0)
        vBottom.anchor = vec(0.5, 1)
        hLeft.anchor = vec(0, 0.5)
        hRight.anchor = vec(1, 0.5)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        this.frame = root.bounds
    }

    override fun layoutChildren() {
        layoutStacks(vTopLeft, hTopLeft)
        layoutStacks(vTopRight, hTopRight)
        layoutStacks(vBottomLeft, hBottomLeft)
        layoutStacks(vBottomRight, hBottomRight)

        layoutStacks(vTop, hTop)
        layoutStacks(vBottom, hBottom)
        layoutStacks(hLeft, vLeft)
        layoutStacks(hRight, vRight)

        vTopLeft.pos = vec(0, 0)
        vTopRight.pos = vec(this.width, 0)
        vBottomLeft.pos = vec(0, this.height)
        vBottomRight.pos = vec(this.width, this.height)

        vTop.pos = vec(this.widthi/2, 0)
        vBottom.pos = vec(this.widthi/2, this.height)
        hLeft.pos = vec(0, this.heighti/2)
        hRight.pos = vec(this.width, this.heighti/2)
    }

    fun layoutStacks(primary: StackLayer, secondary: StackLayer) {
        secondary.width = 0.0
        secondary.fitToBreadth()
        primary.fitToChildren()
        secondary.fitToLength()
    }
}
