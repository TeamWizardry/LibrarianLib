package com.teamwizardry.librarianlib.features.neogui.hud

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.neogui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableView
import net.minecraft.world.BossInfo
import net.minecraftforge.client.event.RenderGameOverlayEvent
import java.util.UUID

class BossHealthHudElement: HudElement(RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
    val leftStack = StackLayout.build().horizontal().reverse().alignRight().alignCenterY().layer()
    val rightStack = StackLayout.build().horizontal().alignLeft().alignCenterY().layer()
    val bottomStack = StackLayout.build().vertical().alignTop().alignCenterX().layer()

    private val _infos = mutableMapOf<UUID, BossInfoLayer>()
    private val seenUUIDs = mutableSetOf<UUID>()
    val bosses = _infos.unmodifiableView()

    private var bottom = 0
    private var index = 0

    init {
        this.add(leftStack, rightStack, bottomStack)
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
            bottom = component.yi + component.heighti
        } else {
            this.seenUUIDs.clear()
            this.bottom = 0
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
            this.heighti = bottom
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()
        val bossesHeight = bosses.map { it.value.y + it.value.height }.max() ?: 0.0
        val bossesLeft = bosses.map { it.value.x }.min() ?: this.root.width/2
        val bossesRight = bosses.map { it.value.x + it.value.width }.max() ?: this.root.width/2

        this.bottomStack.frame = rect(this.root.widthi/2, bossesHeight, 0, 0)
        this.leftStack.frame = rect(bossesLeft, 0, 0, 0)
        this.rightStack.frame = rect(bossesRight, 0, 0, 0)
    }
}

class BossInfoLayer(val uuid: UUID): HudElement(RenderGameOverlayEvent.ElementType.BOSSINFO) {
    val barLeft = StackLayout.build().horizontal().alignRight().alignCenterY().reverse().layer()
    val barRight = StackLayout.build().horizontal().alignLeft().alignCenterY().layer()

    val bossName: GuiLayer = GuiLayer()
    val bossBar: GuiLayer = GuiLayer()
    val bossBarFill: GuiLayer = GuiLayer()
    val overlay: GuiLayer = GuiLayer()
    val overlayFill: GuiLayer = GuiLayer()

    init {
        add(bossName, bossBar, bossBarFill, overlay, overlayFill, barLeft, barRight)
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
        barLeft.frame = rect(0, 14, 0, 0)
        barRight.frame = rect(182, 14, 0, 0)

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
