package com.teamwizardry.librarianlib.mosaic.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import java.awt.Color

internal object LibLibMosaicTest {
    val logManager: ModLogManager = ModLogManager("liblib-mosaic-test", "LibrarianLib Mosaic Test")
    val manager: TestModContentManager = TestModContentManager("liblib-mosaic-test", "Mosaic", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            manager.create<TestItem>("no_mcmeta") {
                name = "No .mcmeta"
                description = "A 32x32 texture with no .mcmeta file"
            }
            manager.create<TestItem>("two_sprites") {
                name = "Two Sprites"
                description = "A spritesheet consisting of two sprites with opposing arrows, drawn so the arrows point together"
            }
            manager.create<TestItem>("sprite_pinning") {
                name = "Sprite Pinning"
                description = "A spritesheet with all the valid combinations of pinned edges."
            }
            manager.create<TestItem>("sprite_caps") {
                name = "Sprite Caps"
                description = "A spritesheet with various sprite cap scenarios"
            }
            manager.create<TestItem>("sprite_animation") {
                name = "Sprite Animation"
                description = "A spritesheet with various types of animation"
            }

            manager.registerCommon()
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        @Suppress("LocalVariableName")
        override fun onInitializeClient() {
            manager.named<TestItem>("no_mcmeta") {
                rightClick.client {
                    Client.minecraft.setScreen(TestScreen {
                        val tex = Mosaic(Identifier("liblib-mosaic-test:textures/gui/no_mcmeta.png"), 32, 32)
                        val sprite = tex.getSprite("")

                        size = vec(32, 32)
                        scale = 4

                        draw {
                            sprite.draw(Matrix4d(matrix), 0f, 0f, 0, Color.WHITE)
                        }
                    })
                }
            }

            manager.named<TestItem>("two_sprites") {
                rightClick.client {
                    Client.minecraft.setScreen(TestScreen {
                        val tex = Mosaic(Identifier("liblib-mosaic-test:textures/gui/two_sprites.png"), 64, 64)
                        val topLeftSprite = tex.getSprite("top_left")
                        val bottomRightSprite = tex.getSprite("bottom_right")

                        size = vec(64, 64)
                        scale = 2

                        draw {
                            val m = Matrix4d(matrix)
                            bottomRightSprite.draw(m, 4f, 4f)
                            topLeftSprite.draw(m, 34f, 34f)
                        }
                    })
                }
            }

            manager.named<TestItem>("sprite_pinning") {
                rightClick.client {
                    Client.minecraft.setScreen(TestScreen {
                        val tex = Mosaic(Identifier("liblib-mosaic-test:textures/gui/edge_pinning.png"), 128, 128)
                        val background = tex.getSprite("bg")

                        size = vec(background.width, background.height)
                        scale = 2

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
                    })
                }
            }

            manager.named<TestItem>("sprite_caps") {
                rightClick.client {
                    Client.minecraft.setScreen(TestScreen {
                        val tex = Mosaic(Identifier("liblib-mosaic-test:textures/gui/sprite_caps.png"), 256, 128)
                        val background = tex.getSprite("background")

                        size = vec(background.width, background.height)
                        scale = 2

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
                    })
                }
            }

            manager.named<TestItem>("sprite_animation") {
                rightClick.client {
                    Client.minecraft.setScreen(TestScreen {
                        val tex = Mosaic(Identifier("liblib-mosaic-test:textures/gui/sprite_animations.png"), 128, 256)
                        val background = tex.getSprite("background")

                        size = vec(background.width, background.height)
                        scale = 2

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
                    })
                }
            }

            manager.registerClient()
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun Sprite.draw(matrix: Matrix4d, x: Number, y: Number, width: Number, height: Number, animTicks: Int, tint: Color) {
            this.draw(matrix, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), animTicks, tint)
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun Sprite.draw(matrix: Matrix4d, x: Number, y: Number, width: Number, height: Number) {
            this.draw(matrix, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun Sprite.draw(matrix: Matrix4d, x: Number, y: Number, animTicks: Int, tint: Color) {
            this.draw(matrix, x.toFloat(), y.toFloat(), animTicks, tint)
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun Sprite.draw(matrix: Matrix4d, x: Number, y: Number) {
            this.draw(matrix, x.toFloat(), y.toFloat())
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
