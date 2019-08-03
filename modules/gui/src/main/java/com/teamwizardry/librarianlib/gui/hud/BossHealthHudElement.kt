package com.teamwizardry.librarianlib.gui.hud

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableView
import net.minecraft.world.BossInfo
import net.minecraftforge.client.event.RenderGameOverlayEvent
import java.util.UUID

class BossHealthHudElement: HudElement(RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
    private val _infos = mutableMapOf<UUID, BossInfoLayer>()
    private val seenUUIDs = mutableSetOf<UUID>()
    val bosses = _infos.unmodifiableView()

    private var bottomY = 0
    private var index = 0

    init {
        autoContentBounds = AutoContentBounds.CONTENTS
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        if(e is RenderGameOverlayEvent.BossInfo) {
            val uuid = e.bossInfo.uniqueId
            seenUUIDs.add(uuid)
            val component = _infos.getOrPut(uuid) {
                BossInfoLayer(uuid).also { this.add(it) }
            }
            component.zIndex = (index++).toDouble()
            component.hudEvent(e)
            bottomY = component.yi + component.heighti
        } else {
            this.seenUUIDs.clear()
            this.bottomY = 0
            this.index = 0
        }
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Post) {
        super.hudEvent(e)
        if(e.type == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            _infos.entries.toList().forEach { (uuid, component) ->
                if (uuid !in seenUUIDs) {
                    _infos.remove(uuid)
                    component.removeFromParent()
                }
            }
            this.heighti = bottomY
        }
    }
}

class BossInfoLayer(val uuid: UUID): HudElement(RenderGameOverlayEvent.ElementType.BOSSINFO) {
    val bossName: HudElement = HudElement()
    val bossBar: HudElement = HudElement()
    val bossBarFill: HudElement = HudElement()
    val overlay: HudElement = HudElement()
    val overlayFill: HudElement = HudElement()

    init {
        add(bossName, bossBar, bossBarFill, overlay, overlayFill)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        e as RenderGameOverlayEvent.BossInfo

        this.frame = rect(e.x, e.y-12, 182, e.increment)

        bossBarFill.isVisible = false
        overlay.isVisible = false
        overlayFill.isVisible = false

        bossBar.frame = rect(0, 12, 182, 5)
        overlay.frame = rect(0, 12, 182, 5)

        if (e.bossInfo.overlay != BossInfo.Overlay.PROGRESS)
        {
            overlay.isVisible = true
        }

        val i = (e.bossInfo.percent * 183).toInt()
        bossBarFill.frame = rect(0, 12, i, 5)
        overlayFill.frame = rect(0, 12, i, 5)

        if (i > 0)
        {
            bossBarFill.isVisible = true

            if (e.bossInfo.overlay != BossInfo.Overlay.PROGRESS)
            {
                overlayFill.isVisible = true
            }
        }

        val stringWidth = this.mc.fontRenderer.getStringWidth(e.bossInfo.name.formattedText)
        bossName.frame = rect(width / 2 - stringWidth / 2, 3, stringWidth, this.mc.fontRenderer.FONT_HEIGHT)
    }
}
