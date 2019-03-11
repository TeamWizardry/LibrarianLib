package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.gui.BossInfoClient
import net.minecraft.client.gui.GuiBossOverlay
import net.minecraft.world.BossInfo
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.UUID

class BossHealthHudElement: HudElement(RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
    private val _infos = mutableMapOf<UUID, ComponentBossInfo>()
    val bosses = _infos.unmodifiableView()

    override fun layoutChildren() {
        super.layoutChildren()
        val mapBossInfos = this.mc.ingameGUI.bossOverlay.mapBossInfos

        _infos.entries.toList().forEach { (uuid, bar) ->
            if(uuid !in mapBossInfos) {
                bar.removeFromParent()
                _infos.remove(uuid)
            }
        }
        mapBossInfos.forEach { (uuid, _) ->
            if(uuid !in _infos) {
                val component = ComponentBossInfo(uuid)
                _infos[uuid] = component
                this.add(component)
            }
        }
        _infos.values.forEach {
            it.setNeedsLayout()
            it.runLayoutIfNeeded() // runLayout() forces children to run layout too, which we don't want
        }
        var y = 0
        mapBossInfos.values.forEach { // the default sorting of `values` is used in MC
            _infos[it.uniqueId]?.also { info ->
                info.pos = vec(0, y)
                y += info.heighti
            }
        }
        this.size = vec(182, y)
        this.pos = vec((root.widthi - this.widthi)/2, 0)
    }

}

class ComponentBossInfo(val uuid: UUID): HudElement(RenderGameOverlayEvent.ElementType.BOSSINFO) {

    val bossName: GuiComponent = GuiComponent()
    val bossBar: GuiComponent = GuiComponent()
    val bossBarFill: GuiComponent = GuiComponent()
    val overlay: GuiComponent = GuiComponent()
    val overlayFill: GuiComponent = GuiComponent()

    init {
        add(bossName, bossBar, bossBarFill, overlay, overlayFill)
    }

    /**
     * @see net.minecraft.client.gui.GuiBossOverlay.render
     */
    override fun layoutChildren() {
        super.layoutChildren()

        val mapBossInfos = this.mc.ingameGUI.bossOverlay.mapBossInfos
        val info = mapBossInfos[uuid] ?: return
        val increment = increments[uuid] ?: run { // null means it was canceled
            size = vec(182, 0)
            isVisible = false
            return
        }
        size = vec(182, increment)

        bossBarFill.isVisible = false
        overlay.isVisible = false
        overlayFill.isVisible = false

        bossBar.frame = rect(0, 12, 182, 5)
        overlay.frame = rect(0, 12, 182, 5)

        if (info.overlay != BossInfo.Overlay.PROGRESS)
        {
            overlay.isVisible = true
        }

        val i = (info.percent * 183.0F).toInt()
        bossBarFill.frame = rect(0, 12, i, 5)
        overlayFill.frame = rect(0, 12, i, 5)

        if (i > 0)
        {
            bossBarFill.isVisible = true

            if (info.overlay != BossInfo.Overlay.PROGRESS)
            {
                overlayFill.isVisible = true
            }
        }

        val stringWidth = this.mc.fontRenderer.getStringWidth(info.name.formattedText)
        bossName.frame = rect(width / 2 - stringWidth / 2, 3, stringWidth, this.mc.fontRenderer.FONT_HEIGHT)
    }

    private companion object {
        init { MinecraftForge.EVENT_BUS.register(this) }

        val increments = mutableMapOf<UUID, Int>()

        @SubscribeEvent
        fun overlay(e: RenderGameOverlayEvent.Pre) {
            if (e.type != RenderGameOverlayEvent.ElementType.ALL) return
            increments.clear()
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun overlay(e: RenderGameOverlayEvent.BossInfo) {
            increments[e.bossInfo.uniqueId] = e.increment
        }
    }
}

private val GuiBossOverlay.mapBossInfos: Map<UUID, BossInfoClient>
    by MethodHandleHelper.delegateForReadOnly(GuiBossOverlay::class.java, "mapBossInfos", "field_184060_g", "g")
