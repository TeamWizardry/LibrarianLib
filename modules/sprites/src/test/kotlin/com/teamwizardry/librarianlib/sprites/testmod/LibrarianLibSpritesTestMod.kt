package com.teamwizardry.librarianlib.sprites.testmod

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.sprites.Sprite
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-sprites-test")
object LibrarianLibSpritesTestMod: TestMod("sprites", "Sprites", logger) {
    init {
        +TestScreenConfig("no_mcmeta", "No .mcmeta") {
            description = "A 32x32 texture with no .mcmeta file"
            size = vec(32, 32)
            client {
//                val sprite = Sprite("librarianlib-sprites-test:textures/gui/no_mcmeta.png".toRl())

                draw {
                    val sprite = Sprite("librarianlib-sprites-test:textures/gui/no_mcmeta.png".toRl())
                    sprite.draw(0, 0f, 0f)
                }
            }
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites Test")
