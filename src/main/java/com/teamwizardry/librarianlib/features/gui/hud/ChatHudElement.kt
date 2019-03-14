package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.hud.mock.MockGuiTextField
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.floorInt
import com.teamwizardry.librarianlib.features.kotlin.identityMapOf
import com.teamwizardry.librarianlib.features.kotlin.identitySetOf
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.util.math.MathHelper
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.gui.GuiTextField
import net.minecraft.entity.player.EntityPlayer

class ChatHudElement: HudElement(RenderGameOverlayEvent.ElementType.CHAT) {
    private val ingameGui get() = Minecraft.getMinecraft().ingameGUI
    private val chatGui get() = ingameGui.chatGUI

    val chatLines = identityMapOf<ChatLine, ChatLineLayer>()
    val chatScrollbar = GuiLayer()
    val inputArea = GuiLayer()
    val field = MockGuiTextField()

    init {
        add(chatScrollbar, inputArea, field)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        e as RenderGameOverlayEvent.Chat
        this.pos = vec(0, 0)

        if (this.mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) {
            this.isVisible = false
        } else {
            val linesToRemove = identitySetOf<ChatLine>()
            linesToRemove.addAll(chatLines.keys)

            val opacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f

            if (chatGui.drawnChatLines.isNotEmpty()) {
                val scale = chatGui.chatScale
                val k = MathHelper.ceil(chatGui.chatWidth.toFloat() / scale)
//                GlStateManager.pushMatrix()
//                GlStateManager.translate(2.0f, 8.0f, 0.0f)
//                GlStateManager.scale(scale, scale, 1.0f)
                var visibleLinesCount = 0

                var visualIndex = 0
                while (visualIndex + chatGui.scrollPos < chatGui.drawnChatLines.size && visualIndex < chatGui.lineCount) {
                    val chatline = chatGui.drawnChatLines[visualIndex + chatGui.scrollPos]

                    val lineAge = ingameGui.updateCounter - chatline.updatedCounter

                    if (lineAge < 200 || chatGui.chatOpen) {
                        var lineOpacity = lineAge / 200.0
                        lineOpacity = 1.0 - lineOpacity
                        lineOpacity = lineOpacity * 10.0
                        lineOpacity = MathHelper.clamp(lineOpacity, 0.0, 1.0)
                        lineOpacity = lineOpacity * lineOpacity
                        var lineOpacityInt = (255.0 * lineOpacity).toInt()

                        if (chatGui.chatOpen) {
                            lineOpacityInt = 255
                        }

                        lineOpacityInt = (lineOpacityInt.toFloat() * opacity).toInt()
                        ++visibleLinesCount

                        if (lineOpacityInt > 3) {
                            val lineLayer = chatLines.getOrPut(chatline) {
                                ChatLineLayer(chatline).also { this.add(it) }
                            }
                            linesToRemove.remove(chatline)
                            val offsetY = -visualIndex * 9
                            lineLayer.frame = rect(e.posX, e.posY + offsetY - 1, k + 6, 9)
                            val s = chatline.chatComponent.formattedText
                            lineLayer.text.frame = rect(e.posX + 2, e.posY, this.mc.fontRenderer.getStringWidth(s), this.mc.fontRenderer.FONT_HEIGHT)
                        }
                    }
                    ++visualIndex
                }

                if (chatGui.chatOpen) {
                    val lineHeight = this.mc.fontRenderer.FONT_HEIGHT
                    val theoreticalHeight = chatGui.drawnChatLines.size * (lineHeight + 1)
                    val actualHeight = visibleLinesCount * (lineHeight + 1)
                    val scrollFraction = chatGui.scrollPos.toDouble() / chatGui.drawnChatLines.size
                    val scrollHeight = floorInt(actualHeight * scrollFraction)
                    val scrollHandleHeight = actualHeight * actualHeight / theoreticalHeight

                    if (theoreticalHeight != actualHeight) {
                        chatScrollbar.isVisible = true
                        chatScrollbar.frame = rect(e.posX - 1, e.posY - (scrollHeight+scrollHandleHeight), 2, scrollHandleHeight)
                    } else {
                        chatScrollbar.isVisible = false
                    }
                }
            }

            linesToRemove.forEach {
                val layer = chatLines[it] ?: return@forEach
                layer.removeFromParent()
                chatLines.remove(it)
            }
        }

        (this.mc.currentScreen as? GuiChat)?.let { guiChat ->
            this.inputArea.isVisible = true
            this.field.isVisible = true
            val field = guiChat.inputField
            this.inputArea.frame = rect(2, root.heighti - 14, root.widthi - 4, 12)
            this.field.pos = vec(field.x, field.y)
            this.field.updateMock(field)
        } ?: run {
            this.inputArea.isVisible = false
            this.field.isVisible = false
        }
    }

    class ChatLineLayer(val chatLine: ChatLine): GuiLayer() {
        val text = GuiLayer()

        init {
            add(text)
        }
    }

    private val GuiNewChat.drawnChatLines by MethodHandleHelper.delegateForReadOnly<GuiNewChat, List<ChatLine>>(
        GuiNewChat::class.java, "drawnChatLines", "field_146253_i", "i")
    private val GuiNewChat.scrollPos by MethodHandleHelper.delegateForReadOnly<GuiNewChat, Int>(
        GuiNewChat::class.java, "scrollPos", "field_146250_j", "j")
    private val GuiChat.inputField by MethodHandleHelper.delegateForReadOnly<GuiChat, GuiTextField>(
        GuiChat::class.java, "inputField", "field_146415_a", "a")
}