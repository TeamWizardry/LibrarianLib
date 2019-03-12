package com.teamwizardry.librarianlib.features.gui.hud

import com.google.common.collect.ComparisonChain
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.kotlin.ceilInt
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiPlayerTabOverlay
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.scoreboard.IScoreCriteria
import net.minecraft.world.GameType
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.util.text.ITextComponent
import java.util.UUID
import net.minecraft.util.math.MathHelper
import net.minecraft.scoreboard.ScoreObjective
import kotlin.math.max

class PlayerListHudElement(type: RenderGameOverlayEvent.ElementType): HudElement(type) {
    val playerItems = mutableMapOf<UUID, PlayerItemLayer>()
    val headerBackground = GuiLayer()
    val headerText = GuiLayer()
    val background = GuiLayer()
    val footerBackground = GuiLayer()
    val footerText = GuiLayer()

    init {
        this.add(headerBackground, headerText, background, footerBackground, footerText)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        val scoreboardIn = this.mc.world.scoreboard
        val scoreObjectiveIn = scoreboardIn.getObjectiveInDisplaySlot(0)
        val handler = mc.player.connection
        val overlayPlayerList = mc.ingameGUI.tabList
        val centerX = root.widthi / 2

        if (mc.gameSettings.keyBindPlayerList.isKeyDown && (!mc.isIntegratedServerRunning || handler.playerInfoMap.size > 1 || scoreObjectiveIn != null)) {
            val nethandlerplayclient = this.mc.player.connection
            val allPlayers = nethandlerplayclient.playerInfoMap.sortedWith( Comparator { p_compare_1_, p_compare_2_ ->
                val scoreplayerteam = p_compare_1_.playerTeam
                val scoreplayerteam1 = p_compare_2_.playerTeam
                ComparisonChain.start().compareTrueFirst(p_compare_1_.gameType !== GameType.SPECTATOR, p_compare_2_.gameType !== GameType.SPECTATOR).compare(if (scoreplayerteam != null) scoreplayerteam!!.name else "", if (scoreplayerteam1 != null) scoreplayerteam1!!.name else "").compare(p_compare_1_.gameProfile.name, p_compare_2_.gameProfile.name).result()
            })

            var maxNameWidth = 0
            var maxScoreWidth = 0

            for (networkplayerinfo in allPlayers) {
                maxNameWidth = Math.max(
                    maxNameWidth,
                    this.mc.fontRenderer.getStringWidth(overlayPlayerList.getPlayerName(networkplayerinfo))
                )

                if (scoreObjectiveIn != null) {
                    val scoreString = " " + scoreboardIn.getOrCreateScore(networkplayerinfo.gameProfile.name, scoreObjectiveIn).scorePoints
                    maxScoreWidth = Math.max(
                        maxScoreWidth,
                        this.mc.fontRenderer.getStringWidth(scoreString)
                    )
                }
            }
            if(scoreObjectiveIn?.renderType == IScoreCriteria.EnumRenderType.HEARTS) {
                maxScoreWidth = 90
            }

            val visiblePlayers = allPlayers.subList(0, Math.min(allPlayers.size, 80))
            val columnCount = ceilInt(visiblePlayers.size / 20.0)
            val rowCount = ceilInt(visiblePlayers.size / columnCount.toDouble())

            val visibleIdSet = visiblePlayers.map { it.gameProfile.id }.toSet()
            playerItems.entries.toList().forEach { (key, value) ->
                if(key !in visibleIdSet) {
                    value.removeFromParent()
                    playerItems.remove(key)
                }
            }

            val columnWidth = Math.min(columnCount * ((if (showPing) 9 else 0) + maxNameWidth + maxScoreWidth + 13), root.widthi - 50) / columnCount
            val columnsLeftX = root.widthi / 2 - (columnWidth * columnCount + (columnCount - 1) * 5) / 2
            var currentY = 10
            var width = columnWidth * columnCount + (columnCount - 1) * 5

            val headerLines: List<String>? = overlayPlayerList.header?.let { header ->
                val list = this.mc.fontRenderer.listFormattedStringToWidth(header.formattedText, root.widthi - 50)

                for (s in list) {
                    width = Math.max(width, this.mc.fontRenderer.getStringWidth(s))
                }

                list
            }

            val footerLines: List<String>? = overlayPlayerList.footer?.let { footer ->
                val list = this.mc.fontRenderer.listFormattedStringToWidth(footer.formattedText, root.widthi - 50)

                for (s1 in list) {
                    width = Math.max(width, this.mc.fontRenderer.getStringWidth(s1))
                }

                list
            }

            if(headerLines != null) {
                headerBackground.frame = rect(
                    centerX - width / 2 - 1,
                    currentY - 1,
                    width + 2,
                    headerLines.size * this.mc.fontRenderer.FONT_HEIGHT + 1
                )

                val textWidth = headerLines.map { this.mc.fontRenderer.getStringWidth(it) }.max() ?: 0
                headerText.frame = rect(
                    centerX - textWidth/2, currentY,
                    textWidth, headerLines.size * this.mc.fontRenderer.FONT_HEIGHT
                )
                currentY += headerLines.size * this.mc.fontRenderer.FONT_HEIGHT + 1
            } else {
                headerBackground.frame = rect(centerX, currentY - 1, 0, 0)
                headerText.frame = rect(centerX, currentY - 1, 0, 0)
            }

            background.frame = rect(centerX - width / 2 - 1, currentY - 1, width + 2, rowCount * 9)

            visiblePlayers.forEachIndexed { i, player ->
                val playerItem = playerItems.getOrPut(player.gameProfile.id) {
                    val component = PlayerItemLayer(player.gameProfile.id)
                    this.add(component)
                    component
                }

                val columnIndex = i / rowCount
                val rowIndex = i % rowCount
                val playerX = columnsLeftX + columnIndex * columnWidth + columnIndex * 5
                val playerY = currentY + rowIndex * 9

                playerItem.frame = rect(playerX, playerY, columnWidth, 8)
                playerItem.update(player, columnWidth, maxNameWidth, maxScoreWidth)
            }

            if (footerLines != null) {
                currentY += rowCount * 9 + 1
                footerBackground.frame = rect(
                    centerX - width / 2 - 1, currentY - 1,
                    width + 2, footerLines.size * this.mc.fontRenderer.FONT_HEIGHT + 1
                )

                val textWidth = footerLines.map { this.mc.fontRenderer.getStringWidth(it) }.max() ?: 0
                footerText.frame = rect(
                    centerX - textWidth/2, currentY,
                    textWidth, footerLines.size * this.mc.fontRenderer.FONT_HEIGHT
                )
                currentY += footerLines.size * this.mc.fontRenderer.FONT_HEIGHT
            } else {
                footerBackground.frame = rect(centerX, currentY - 1, 0, 0)
                footerText.frame = rect(centerX, currentY - 1, 0, 0)
            }
        }
    }

    class PlayerItemLayer(val id: UUID): GuiLayer() {
        private val mc: Minecraft get() = Minecraft.getMinecraft()

        val playerHead = GuiLayer()
        val playerName = GuiLayer()
        val ping = GuiLayer()
        val scoreboardValue = ScoreboardValueLayer()

        init {
            this.add(playerHead, playerName, ping, scoreboardValue)
        }

        fun update(playerInfo: NetworkPlayerInfo, columnWidth: Int, maxNameWidth: Int, maxScoreWidth: Int) {
            val scoreboardIn = this.mc.world.scoreboard
            val scoreObjectiveIn = scoreboardIn.getObjectiveInDisplaySlot(0)
            val overlayPlayerList = mc.ingameGUI.tabList
            val gameprofile = playerInfo.gameProfile
            var currentX = 0

            if (showPing) {
                playerHead.frame = rect(currentX, 0, 8, 8)

                currentX += 9
            }

            playerName.frame = rect(
                currentX, 0,
                this.mc.fontRenderer.getStringWidth(overlayPlayerList.getPlayerName(playerInfo)),
                this.mc.fontRenderer.FONT_HEIGHT
            )
            currentX += maxNameWidth + 1

            if (scoreObjectiveIn != null && playerInfo.gameType != GameType.SPECTATOR && maxScoreWidth > 5) {
                scoreboardValue.update(scoreObjectiveIn, gameprofile.name,
                    currentX,
                    currentX + maxScoreWidth,
                    playerInfo)
            }

            ping.frame = rect(columnWidth - 11, 0, 10, 8)
        }
    }

    class ScoreboardValueLayer: GuiLayer() {
        private val mc: Minecraft get() = Minecraft.getMinecraft()

        fun update(objective: ScoreObjective, name: String, nameEndX: Int, scoreEndX: Int, info: NetworkPlayerInfo) {
            val score = objective.scoreboard.getOrCreateScore(name, objective).scorePoints

            if (objective.renderType === IScoreCriteria.EnumRenderType.HEARTS) {
                val filledHearts = MathHelper.ceil(Math.max(score, info.displayHealth).toFloat() / 2.0f)
                val hearts = Math.max(score / 2, Math.max(info.displayHealth / 2, 10))

                if (filledHearts > 0) {
                    val spacePerHeart = Math.min((scoreEndX - nameEndX - 4).toFloat() / hearts.toFloat(), 9.0f)

                    if (spacePerHeart > 3.0f) {
                        this.frame = rect(nameEndX, 0, max(hearts, filledHearts) * spacePerHeart, 9)
                    } else {
                        var s = "" + score.toFloat() / 2.0f

                        if (scoreEndX - this.mc.fontRenderer.getStringWidth(s + "hp") >= nameEndX) {
                            s += "hp"
                        }

                        this.frame = rect((scoreEndX + nameEndX) / 2 - this.mc.fontRenderer.getStringWidth(s) / 2, 0,
                            this.mc.fontRenderer.getStringWidth(s), this.mc.fontRenderer.FONT_HEIGHT)
                    }
                } else {
                    this.frame = rect(nameEndX, 0, 0, 9)
                }
            } else {
                val width = this.mc.fontRenderer.getStringWidth("$score")
                this.frame = rect(scoreEndX - width, 0, width, this.mc.fontRenderer.FONT_HEIGHT)
            }
        }
    }

    private companion object {
        val showPing: Boolean get() =
            Minecraft.getMinecraft().isIntegratedServerRunning ||
                Minecraft.getMinecraft().connection?.networkManager?.isEncrypted == true
    }
}

private val GuiPlayerTabOverlay.footer by MethodHandleHelper.delegateForReadOnly<GuiPlayerTabOverlay, ITextComponent?>(
    GuiPlayerTabOverlay::class.java, "footer", "field_175255_h", "h")
private val GuiPlayerTabOverlay.header by MethodHandleHelper.delegateForReadOnly<GuiPlayerTabOverlay, ITextComponent?>(
    GuiPlayerTabOverlay::class.java, "header", "field_175256_i", "i")
private val GuiPlayerTabOverlay.lastTimeOpened by MethodHandleHelper.delegateForReadOnly<GuiPlayerTabOverlay, Long>(
    GuiPlayerTabOverlay::class.java, "lastTimeOpened", "field_175253_j", "j")
