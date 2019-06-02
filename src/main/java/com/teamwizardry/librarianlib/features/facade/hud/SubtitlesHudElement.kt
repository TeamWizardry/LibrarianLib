package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.identityMapOf
import com.teamwizardry.librarianlib.features.kotlin.toIdentitySet
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.GuiSubtitleOverlay
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.client.Minecraft

class SubtitlesHudElement: HudElement(RenderGameOverlayEvent.ElementType.SUBTITLES) {
    val overlaySubtitle get() = this.mc.ingameGUI.overlaySubtitle

    val subtitles = identityMapOf<GuiSubtitleOverlay.Subtitle, SubtitleLayer>()
    val panel = GuiLayer()

    init {
        this.add(panel)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        val player = this.mc.player
        val thisSubtitles = overlaySubtitle.subtitles.toMutableList() // copy so we can safely emulate MC's removal

        val subtitlesToRemove = this.subtitles.keys.toIdentitySet()

        if (this.mc.gameSettings.showSubtitles && !thisSubtitles.isEmpty()) {
            val eyePos = vec(player.posX, player.posY + player.getEyeHeight(), player.posZ)
            val lookNormal = vec(0.0, 0.0, -1.0).rotatePitch(-player.rotationPitch * 0.017453292f).rotateYaw(-player.rotationYaw * 0.017453292f)
            val upNormal = vec(0.0, 1.0, 0.0).rotatePitch(-player.rotationPitch * 0.017453292f).rotateYaw(-player.rotationYaw * 0.017453292f)
            val sideNormal = lookNormal.crossProduct(upNormal)
            var maxWidth = 0
            val iterator = thisSubtitles.iterator()

            while (iterator.hasNext()) {
                val subtitle = iterator.next()

                if (subtitle.startTime + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove()
                } else {
                    maxWidth = Math.max(maxWidth, this.mc.fontRenderer.getStringWidth("< ${subtitle.string} >"))
                }
            }

            val lineHeight = this.mc.fontRenderer.FONT_HEIGHT
            panel.frame = rect(
                root.widthi - maxWidth - 2,
                root.heighti - 30 - (thisSubtitles.size-0.5 + 10) * (lineHeight + 1),
                maxWidth, lineHeight
            )

            thisSubtitles.forEachIndexed { i, subtitle ->
                val layer = this.subtitles.getOrPut(subtitle) {
                    SubtitleLayer().also { this.add(it) }
                }
                subtitlesToRemove.remove(subtitle)
                val eyeToSoundNormal = subtitle.location.subtract(eyePos).normalize()
                val sideProjection = -sideNormal.dotProduct(eyeToSoundNormal)
                val lookProjection = -lookNormal.dotProduct(eyeToSoundNormal)
                val isAhead = lookProjection > 0.5
                val lineWidth = this.mc.fontRenderer.getStringWidth(subtitle.string)

                layer.frame = rect(
                    root.widthi - maxWidth - 1,
                    root.heighti - 29 - (i+0.5) * (lineHeight + 1),
                    maxWidth-1, lineHeight-1
                )

                layer.background.frame = rect(
                    -1, -1,
                    maxWidth + 1, lineHeight + 1
                )
                layer.left.frame = rect(
                    0, 0,
                    this.mc.fontRenderer.getStringWidth("<"), lineHeight-1
                )
                layer.right.frame = rect(
                    maxWidth - this.mc.fontRenderer.getStringWidth(">") - 1, 0,
                    this.mc.fontRenderer.getStringWidth(">"), lineHeight-1
                )
                layer.text.frame = rect(
                    (maxWidth - lineWidth) / 2, 0,
                    lineWidth, lineHeight-1
                )

                layer.left.isVisible = !isAhead && sideProjection < 0
                layer.right.isVisible = !isAhead && sideProjection > 0
            }
        }

        subtitlesToRemove.forEach {
            this.subtitles.remove(it)?.removeFromParent()
        }
    }

    private val GuiIngame.overlaySubtitle by MethodHandleHelper.delegateForReadOnly<GuiIngame, GuiSubtitleOverlay>(
        GuiIngame::class.java, "overlaySubtitle", "field_184049_t")
    private val GuiSubtitleOverlay.subtitles by MethodHandleHelper.delegateForReadOnly<GuiSubtitleOverlay, List<GuiSubtitleOverlay.Subtitle>>(
        GuiSubtitleOverlay::class.java, "subtitles", "field_184070_f")

    class SubtitleLayer: GuiLayer() {
        val background = GuiLayer()
        val left = GuiLayer()
        val right = GuiLayer()
        val text = GuiLayer()

        init {
            this.add(background, left, right, text)
        }
    }
}