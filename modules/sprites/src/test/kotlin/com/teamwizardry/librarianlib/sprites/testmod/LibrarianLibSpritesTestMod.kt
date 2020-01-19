@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.sprites.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.sprites.ISprite
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
//                    val sprite = Sprite("librarianlib-sprites-test:textures/gui/no_mcmeta.png".toRl())

                    draw {
//                        sprite.draw(0, 0f, 0f)
                    }
                }
            }
        }

        +TestScreenConfig("two_sprites", "Two Sprites") {
            description = """
                A spritesheet consisting of two sprites with opposing arrows, drawn so the arrows point together
            """.trimIndent()
            size = vec(64, 64)
            scale = 2
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

        +TestScreenConfig("sprite_pinning", "Sprite Pinning") {
            description = """
                A spritesheet with all the valid combinations of pinned edges.
            """.trimIndent()
            scale = 2
            lazyConfig {
                client {
                    val tex = Texture("librarianlib-sprites-test:textures/gui/edge_pinning.png".toRl(), 128, 128)
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

        +TestScreenConfig("sprite_caps", "Sprite Caps") {
            description = """
                A spritesheet with various sprite cap scenarios
            """.trimIndent()
            scale = 2
            lazyConfig {
                client {
                    val tex = Texture("librarianlib-sprites-test:textures/gui/sprite_caps.png".toRl(), 256, 128)
                    val background = tex.getSprite("background")
                    size = vec(background.width, background.height)

                    val xy_plain = tex.getSprite("xy_plain")
                    val x_plain = tex.getSprite("x_plain")
                    val y_plain = tex.getSprite("y_plain")

                    val xy_all = tex.getSprite("xy_all")
                    val xy_topleft = tex.getSprite("xy_topleft")
                    val xy_bottomright = tex.getSprite("xy_bottomright")

                    val xy_pinned = tex.getSprite("xy_pinned")
                    val xy_not_pinned = tex.getSprite("xy_not_pinned")

                    val x_all = tex.getSprite("x_all")
                    val x_left = tex.getSprite("x_left")
                    val x_right = tex.getSprite("x_right")

                    val y_all = tex.getSprite("y_all")
                    val y_top = tex.getSprite("y_top")
                    val y_bottom = tex.getSprite("y_bottom")

                    draw {
                        background.draw(0, 0f, 0f)
                        xy_plain.draw(0, 16f, 8f)
                        x_plain.draw(0, 81, 9)
                        y_plain.draw(0, 50, 58)

                        xy_all.draw(0, 13, 22, 18, 18)
                        xy_topleft.draw(0, 13, 42, 18, 18)
                        xy_bottomright.draw(0, 13, 62, 18, 18)

                        xy_pinned.draw(0, 38, 8, 20, 20)
                        xy_not_pinned.draw(0, 38, 30, 20, 20)

                        x_all.draw(0, 78, 17, 18, 6)
                        x_left.draw(0, 78, 25, 18, 6)
                        x_right.draw(0, 78, 33, 18, 6)
                        x_all.draw(0, 83, 41, 8, 6)
                        x_all.draw(0, 85, 49, 4, 6)

                        y_all.draw(0, 58, 55, 6, 18)
                        y_bottom.draw(0, 66, 55, 6, 18)
                        y_top.draw(0, 74, 55, 6, 18)
                        y_all.draw(0, 82, 60, 6, 8)
                        y_all.draw(0, 90, 62, 6, 4)
                    }
                }
            }
        }

        +TestScreenConfig("sprite_animation", "Sprite Animation") {
            description = """
                A spritesheet with various types of animation
            """.trimIndent()
            scale = 2
            lazyConfig {
                client {
                    val tex = Texture("librarianlib-sprites-test:textures/gui/sprite_animations.png".toRl(), 128, 256)
                    val background = tex.getSprite("background")
                    size = vec(background.width, background.height)

                    val simple = tex.getSprite("simple")
                    val marquee = tex.getSprite("marquee")
                    val arrow = tex.getSprite("arrow")
                    val bounce = tex.getSprite("bounce")

                    draw {
                        background.draw(0, 0f, 0f)
                        simple.draw(Client.time.ticks, 5f, 17f)
                        marquee.draw(Client.time.ticks, 13f, 22f)
                        arrow.draw(Client.time.ticks, 24f, 5f)
                        bounce.draw(Client.time.ticks, 5f, 5f)
                    }
                }
            }
        }
    }

    private inline fun ISprite.draw(animTicks: Int, x: Number, y: Number, width: Number, height: Number) {
        this.draw(animTicks, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    private inline fun ISprite.draw(animTicks: Int, x: Number, y: Number) {
        this.draw(animTicks, x.toFloat(), y.toFloat())
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites Test")
