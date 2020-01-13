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
            scale = 4
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
            scale = 4
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

        +TestScreenConfig("sprite_pinning", "Sprite pinning") {
            description = """
                A spritesheet with all the valid combinations of pinned edges.
            """.trimIndent()
            scale = 4
            lazyConfig {
                client {
                    val tex = Texture("librarianlib-sprites-test:textures/gui/edge_pinning.png".toRl(), 64, 64)
                    val background = tex.getSprite("bg")
                    size = vec(background.width, background.height)

                    val none = tex.getSprite("none")
                    val L_T_R_B = tex.getSprite("L_T_R_B")
                    val x_T_R_B = tex.getSprite("x_T_R_B")
                    val L_x_R_B = tex.getSprite("L_x_R_B")
                    val L_T_x_B = tex.getSprite("L_T_x_B")
                    val L_T_R_x = tex.getSprite("L_T_R_x")

                    val L_T_x_x = tex.getSprite("L_T_x_x")
                    val x_T_R_x = tex.getSprite("x_T_R_x")
                    val x_x_R_B = tex.getSprite("x_x_R_B")
                    val L_x_x_B = tex.getSprite("L_x_x_B")

                    draw {
                        background.draw(0, 0f, 0f)
                        none.draw(0, 3f, 3f)

                        L_T_R_B.draw(0, 14f, 4f, 12f, 12f)
                        x_T_R_B.draw(0, 14f, 20f, 12f, 12f)
                        L_x_R_B.draw(0, 30f, 20f, 12f, 12f)
                        L_T_x_B.draw(0, 46f, 20f, 12f, 12f)
                        L_T_R_x.draw(0, 62f, 20f, 12f, 12f)

                        L_T_x_x.draw(0, 14f, 36f, 12f, 12f)
                        x_T_R_x.draw(0, 30f, 36f, 12f, 12f)
                        x_x_R_B.draw(0, 46f, 36f, 12f, 12f)
                        L_x_x_B.draw(0, 62f, 36f, 12f, 12f)
                    }
                }
            }
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites Test")
