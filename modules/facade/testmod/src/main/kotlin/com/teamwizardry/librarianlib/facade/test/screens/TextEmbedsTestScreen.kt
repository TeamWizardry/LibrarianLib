package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.SpriteEmbed
import com.teamwizardry.librarianlib.mosaic.Mosaic
import dev.thecodewarrior.bitfont.typesetting.MutableAttributedString
import dev.thecodewarrior.bitfont.typesetting.TextAttribute
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color

class TextEmbedsTestScreen(title: Text): FacadeScreen(title) {
    init {
        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 8, 8)
        val dirtEmbed = SpriteEmbed(9, 7, 1, 0, -7, dirt.getSprite(""), false)
        val stone = Mosaic(Identifier("minecraft:textures/block/stone.png"), 8, 8)
        val stoneEmbed = SpriteEmbed(9, 7, 1, 0, -7, stone.getSprite(""), false)
        val diamond = Mosaic(Identifier("minecraft:textures/item/diamond.png"), 8, 8)
        val diamondEmbed = SpriteEmbed(9, 7, 1, 0, -7, diamond.getSprite(""), false)

        val bg = RectLayer(Color.WHITE, 0, 0, 300, 200)
        val text = TextLayer(25, 25, 250, 150, "")
        val str = MutableAttributedString("1x")
            .append("\uE000", TextAttribute.textEmbed to dirtEmbed)
            .append("Dirt + 1x")
            .append("\uE000", TextAttribute.textEmbed to stoneEmbed)
            .append("Stone = 1x")
            .append("\uE000", TextAttribute.textEmbed to diamondEmbed)
            .append("Diamond")
        text.attributedText = str
        main.size = bg.size
        main.add(bg, text)
    }

    companion object {

    }
}