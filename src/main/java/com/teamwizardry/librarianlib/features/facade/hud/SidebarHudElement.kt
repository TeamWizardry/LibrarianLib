package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.util.text.TextFormatting
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.client.GuiIngameForge.renderObjective
import net.minecraft.scoreboard.ScoreObjective

class SidebarHudElement: HudElement(RenderGameOverlayEvent.ElementType.CHAT) { // CHAT is the closest event after the sidebar renders
    var objectiveRow = GuiLayer()
    var objectiveTitle = GuiLayer()
    var scores = emptyList<ScoreLayer>()
        private set
    var background = GuiLayer()

    init {
        this.add(background, objectiveRow, objectiveTitle)
        autoContentBounds = AutoContentBounds.CONTENTS
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        val scoreboard = this.mc.world.scoreboard
        var objective: ScoreObjective? = null
        val scoreplayerteam = scoreboard.getPlayersTeam(mc.player.name)
        if (scoreplayerteam != null) {
            val slot = scoreplayerteam.color.colorIndex
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot)
        }
        val scoreobjective1 = objective ?: scoreboard.getObjectiveInDisplaySlot(1)
        if (renderObjective && scoreobjective1 != null) {
            this.renderScoreboard(scoreobjective1)
        } else {
            this.isVisible = false
        }
    }

    fun renderScoreboard(objective: ScoreObjective) {
        val scoreboard = objective.scoreboard
        val fontRenderer = this.mc.fontRenderer
        val scores = scoreboard.getSortedScores(objective).filter { it?.playerName?.startsWith("#") != true }.take(15)

        var textWidth = fontRenderer.getStringWidth(objective.displayName)

        for (score in scores) {
            val scoreplayerteam = scoreboard.getPlayersTeam(score.playerName)
            val s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.playerName) + ": " + TextFormatting.RED + score.scorePoints
            textWidth = Math.max(textWidth, fontRenderer.getStringWidth(s))
        }

        val height = scores.size * fontRenderer.FONT_HEIGHT
        val j1 = root.heighti / 2 + height / 3
        val left = root.widthi - textWidth - 3
        background.frame = rect(
            left - 2,
            j1 - (scores.size + 1) * fontRenderer.FONT_HEIGHT - 1,
            textWidth + 4,
            (scores.size + 1) * fontRenderer.FONT_HEIGHT + 1
        )

        val scoreSet = scores.map { it.objective.name to it.playerName }.toSet()
        val scoreLayers = this.scores.associateByTo(mutableMapOf()) { it.objective to it.playerName }
        scoreLayers.entries.forEach { (key, value) ->
            if(key !in scoreSet) {
                value.removeFromParent()
                scoreLayers.remove(key)
            }
        }

        val newScores = mutableListOf<ScoreLayer>()

        var row = 0
        for (score in scores) {
            row++

            val layer = scoreLayers.getOrPut(score.objective.name to score.playerName) {
                ScoreLayer(score.objective.name, score.playerName).also { this.add(it) }
            }
            newScores.add(layer)

            val playerName = ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(score.playerName), score.playerName)
            val y = j1 - row * fontRenderer.FONT_HEIGHT

            layer.frame = rect(left - 2, y, textWidth + 4, fontRenderer.FONT_HEIGHT)

            val playerWidth = fontRenderer.getStringWidth(playerName)
            layer.nameText.frame = rect(2, 0, playerWidth, fontRenderer.FONT_HEIGHT)

            val scoreWidth = fontRenderer.getStringWidth("${score.scorePoints}")
            layer.scoreText.frame = rect(textWidth + 4 - scoreWidth, 0, scoreWidth, fontRenderer.FONT_HEIGHT)

            if (row == scores.size) {
                objectiveRow.frame = rect(left - 2, y - fontRenderer.FONT_HEIGHT - 1, textWidth+4, fontRenderer.FONT_HEIGHT)
//                drawRect(left - 2, y - 1, right, y, 1342177280) // separator

                val titleWidth = fontRenderer.getStringWidth(objective.displayName)
                objectiveTitle.frame = rect(left+textWidth/2 - titleWidth / 2, y - fontRenderer.FONT_HEIGHT, titleWidth, fontRenderer.FONT_HEIGHT)
            }
        }

        this.scores = newScores
    }

    class ScoreLayer(val objective: String, val playerName: String): GuiLayer() {
        val nameText = GuiLayer()
        val scoreText = GuiLayer()

        init {
            this.add(nameText, scoreText)
        }
    }
}
