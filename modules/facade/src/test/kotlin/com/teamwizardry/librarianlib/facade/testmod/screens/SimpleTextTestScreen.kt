package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.BitfontFormat
import net.minecraft.util.text.ITextComponent
import java.awt.Color

class SimpleTextTestScreen(title: ITextComponent): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 300)
        // https://minecraft.gamepedia.com/File:Minecraft_Formatting.gif
        val text = TextLayer(25, 25, 200, 300, "")
        text.attributedText = BitfontFormat.convertMC("""
                §nMinecraft Formatting

                §r§00 §11 §22 §33
                §44 §55 §66 §77
                §88 §99 §aa §bb
                §cc §dd §ee §ff

                §r§0k §kMinecraft
                
                §rl §lé ü ñ î
                §rl §lé ü ñ î
                
                §rm §mMinecraft
                §rn §nMinecraft
                §ro §oMinecraft
                §rr §rMinecraft
            """.trimIndent())
        text.updateText()
        main.size = bg.size
        main.add(bg, text)
    }
}