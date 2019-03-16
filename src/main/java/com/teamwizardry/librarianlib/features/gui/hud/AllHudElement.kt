package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class AllHudElement: HudElement(RenderGameOverlayEvent.ElementType.ALL) {
    val topCenter = StackLayout.build().alignTop().vertical().alignCenterX().layer()

    val topLeft = StackLayout.build().alignTop().vertical().alignLeft().layer()
    val topRight = StackLayout.build().alignTop().vertical().alignRight().layer()

    val middleLeft = StackLayout.build().alignRight().horizontal().alignCenterY().layer()
    val middleRight = StackLayout.build().alignRight().horizontal().alignCenterY().reverse().layer()

    val bottomLeft = StackLayout.build().alignBottom().vertical().alignLeft().reverse().layer()
    val bottomRight = StackLayout.build().alignBottom().vertical().alignRight().reverse().layer()

    init {
        this.add(topCenter, topLeft, topRight, middleLeft, middleRight, bottomLeft, bottomRight)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        this.frame = root.bounds
    }

    override fun layoutChildren() {
        super.layoutChildren()
        topCenter.frame = rect(this.widthi/2, 0, 0, 0)

        topLeft.frame = rect(0, 0, 0, 0)
        topRight.frame = rect(this.width, 0, 0, 0)

        middleLeft.frame = rect(0, this.heighti/2, 0, 0)
        middleRight.frame = rect(this.width, this.heighti/2, 0, 0)

        bottomLeft.frame = rect(0, this.heighti, 0, 0)
        bottomRight.frame = rect(this.width, this.heighti, 0, 0)
    }
}
