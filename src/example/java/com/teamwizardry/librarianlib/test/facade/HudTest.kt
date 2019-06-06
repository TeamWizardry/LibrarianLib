package com.teamwizardry.librarianlib.test.facade

import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.hud.GuiHud
import com.teamwizardry.librarianlib.features.facade.hud.IHudReloadListener
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft

object HudTest: IHudReloadListener {
    val texture = Texture("librarianlibtest:textures/guis/hud.png".toRl(), 256, 256)
    val warning = texture.getSprite("warning")

    init {
        GuiHud.registerReloadListener(this)
        this.reloadHud()
    }

    override fun reloadHud() {
        val healthWarning = SpriteLayer(warning, 0, 0)
        val airWarning = SpriteLayer(warning, 0, 0)

        GuiHud.health.leftStack.add(healthWarning)
        GuiHud.air.rightStack.add(airWarning)
        GuiHud.root.BUS.hook<GuiLayerEvents.PreFrameEvent> {
            val player = Minecraft.getMinecraft().player
            healthWarning.size = vec(4, 9)
            healthWarning.isVisible = player.health < 6
            val air = player.air / 300.0
            airWarning.isVisible = player.isInsideOfMaterial(Material.WATER) && air < 0.75
        }
    }
}