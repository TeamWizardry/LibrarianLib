package com.teamwizardry.librarianlib.features.gui.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.layers.DropShadowLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

open class SimpleWindow(width: Int, height: Int): GuiWindow(width, height) {
    private val dropShadowLayer = DropShadowLayer(0, 0, width, height, 8)
    var showDropShadow: Boolean
        get() = dropShadowLayer.isVisible
        set(value) { dropShadowLayer.isVisible = value }

    init {
        dropShadowLayer.color = PastryTexture.shadowColor
        dropShadowLayer.zIndex = Double.NEGATIVE_INFINITY
        dropShadowLayer.anchor = vec(0.5, 0.5)
        add(dropShadowLayer)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        dropShadowLayer.size = this.size + vec(dropShadowLayer.radius/2.0, 0)
        dropShadowLayer.pos = this.size/2 + vec(0, dropShadowLayer.radius/2.0)
    }
}