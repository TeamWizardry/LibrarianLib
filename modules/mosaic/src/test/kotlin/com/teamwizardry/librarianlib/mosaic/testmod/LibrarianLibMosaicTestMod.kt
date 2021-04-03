@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.mosaic.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.mosaic.ISprite
import com.teamwizardry.librarianlib.mosaic.LibrarianLibMosaicModule
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import java.awt.Color

@Mod("ll-mosaic-test")
object LibrarianLibSpritesTestMod: TestMod(LibrarianLibMosaicModule) {
    init {
        +TestScreenConfig("no_mcmeta", "No .mcmeta") {
            description = "A 32x32 texture with no .mcmeta file"
            size = vec(32, 32)
            scale = 4
            lazyConfig {
                client {
                    val tex = Mosaic(loc("ll-mosaic-test:textures/gui/no_mcmeta.png"), 32, 32)
                    val sprite = tex.getSprite("")

                    draw {
                        sprite.draw(Matrix4d(matrix), 0f, 0f, 0, Color.WHITE)
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
                    val tex = Mosaic(loc("ll-mosaic-test:textures/gui/two_sprites.png"), 64, 64)
                    val topLeftSprite = tex.getSprite("top_left")
                    val bottomRightSprite = tex.getSprite("bottom_right")

                    draw {
                        val m = Matrix4d(matrix)
                        bottomRightSprite.draw(m, 4f, 4f)
                        topLeftSprite.draw(m, 34f, 34f)
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
                    val tex = Mosaic(loc("ll-mosaic-test:textures/gui/edge_pinning.png"), 128, 128)
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
                        val m = Matrix4d(matrix)

                        background.draw(m, 0f, 0f)
                        none.draw(m, 3f, 3f)

                        L_T_R_B.draw(m, 14f, 4f, 12f, 12f)
                        x_T_R_B.draw(m, 14f, 20f, 12f, 12f)
                        L_x_R_B.draw(m, 30f, 20f, 12f, 12f)
                        L_T_x_B.draw(m, 46f, 20f, 12f, 12f)
                        L_T_R_x.draw(m, 62f, 20f, 12f, 12f)

                        L_T_x_x.draw(m, 14f, 36f, 12f, 12f)
                        x_T_R_x.draw(m, 30f, 36f, 12f, 12f)
                        x_x_R_B.draw(m, 46f, 36f, 12f, 12f)
                        L_x_x_B.draw(m, 62f, 36f, 12f, 12f)
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
                    val tex = Mosaic(loc("ll-mosaic-test:textures/gui/sprite_caps.png"), 256, 128)
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
                        val m = Matrix4d(matrix)
                        background.draw(m, 0f, 0f)
                        xy_plain.draw(m, 16f, 8f)
                        x_plain.draw(m, 81, 9)
                        y_plain.draw(m, 50, 58)

                        xy_all.draw(m, 13, 22, 18, 18)
                        xy_topleft.draw(m, 13, 42, 18, 18)
                        xy_bottomright.draw(m, 13, 62, 18, 18)

                        xy_pinned.draw(m, 38, 8, 20, 20)
                        xy_not_pinned.draw(m, 38, 30, 20, 20)

                        x_all.draw(m, 78, 17, 18, 6)
                        x_left.draw(m, 78, 25, 18, 6)
                        x_right.draw(m, 78, 33, 18, 6)
                        x_all.draw(m, 83, 41, 8, 6)
                        x_all.draw(m, 85, 49, 4, 6)

                        y_all.draw(m, 58, 55, 6, 18)
                        y_bottom.draw(m, 66, 55, 6, 18)
                        y_top.draw(m, 74, 55, 6, 18)
                        y_all.draw(m, 82, 60, 6, 8)
                        y_all.draw(m, 90, 62, 6, 4)
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
                    val tex = Mosaic(loc("ll-mosaic-test:textures/gui/sprite_animations.png"), 128, 256)
                    val background = tex.getSprite("background")
                    size = vec(background.width, background.height)

                    val simple = tex.getSprite("simple")
                    val marquee = tex.getSprite("marquee")
                    val arrow = tex.getSprite("arrow")
                    val bounce = tex.getSprite("bounce")

                    draw {
                        val m = Matrix4d(matrix)
                        background.draw(m, 0f, 0f, Client.time.ticks, Color.WHITE)
                        simple.draw(m, 5f, 17f, Client.time.ticks, Color.WHITE)
                        marquee.draw(m, 13f, 22f, Client.time.ticks, Color.WHITE)
                        arrow.draw(m, 24f, 5f, Client.time.ticks, Color.WHITE)
                        bounce.draw(m, 5f, 5f, Client.time.ticks, Color.WHITE)
                    }
                }
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun ISprite.draw(matrix: Matrix4d, x: Number, y: Number, width: Number, height: Number, animTicks: Int, tint: Color) {
        this.draw(matrix, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), animTicks, tint)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun ISprite.draw(matrix: Matrix4d, x: Number, y: Number, width: Number, height: Number) {
        this.draw(matrix, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun ISprite.draw(matrix: Matrix4d, x: Number, y: Number, animTicks: Int, tint: Color) {
        this.draw(matrix, x.toFloat(), y.toFloat(), animTicks, tint)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun ISprite.draw(matrix: Matrix4d, x: Number, y: Number) {
        this.draw(matrix, x.toFloat(), y.toFloat())
    }
}

internal val logger = LibrarianLibSpritesTestMod.makeLogger(null)
