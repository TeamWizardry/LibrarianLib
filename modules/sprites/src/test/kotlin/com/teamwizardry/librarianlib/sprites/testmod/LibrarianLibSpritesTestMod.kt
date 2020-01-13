package com.teamwizardry.librarianlib.sprites.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.sprites.Sprite
import com.teamwizardry.librarianlib.sprites.Texture
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-sprites-test")
object LibrarianLibSpritesTestMod: TestMod("sprites", "Sprites", logger) {
    init {
        +TestScreenConfig("no_mcmeta", "No .mcmeta") {
            description = "A 32x32 texture with no .mcmeta file"
            size = vec(32, 32)
            lazyConfig {
                client {
                    val sprite = Sprite("librarianlib-sprites-test:textures/gui/no_mcmeta.png".toRl())

                    draw {
                        sprite.draw(0, 0f, 0f)
                    }
                }
            }
        }
        +TestScreenConfig("two_sprites", "Two sprites") {
            description = """
                A spritesheet consisting of two sprites with opposing arrows, drawn so the arrows point together
            """.trimIndent()
            size = vec(64, 64)
            lazyConfig {
                client {
                    val tex = Texture("librarianlib-sprites-test:textures/gui/two_sprites.png".toRl(), 64, 64)
                    val topLeftSprite = tex.getSprite("top_left")
                    val bottomRightSprite = tex.getSprite("bottom_right")

                    draw {
                        bottomRightSprite.draw(0, 4f, 4f)
                        topLeftSprite.draw(0, 34f, 34f)
                    }
                }
            }
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites Test")
