@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import com.teamwizardry.librarianlib.facade.testmod.screens.*
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestScreen
import com.teamwizardry.librarianlib.facade.testmod.value.RMValueTests
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-facade-test")
object LibrarianLibSpritesTestMod: TestMod("facade", "Facade", logger) {
    init {
        +FacadeScreenConfig("empty", "Empty") { _ -> }

        +FacadeScreenConfig("sprite", "Simple Sprite") { screen ->
            val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16)
            val layer = SpriteLayer(dirt.getSprite(""))
            screen.facade.root.add(layer)
        }

        +FacadeScreenConfig("layer_transform", "Layer Transform") { screen ->
            val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16).getSprite("")
            val stone = Mosaic("minecraft:textures/block/stone.png".toRl(), 16, 16).getSprite("")
            val layer = SpriteLayer(dirt)
            layer.pos = vec(32, 32)
            layer.rotation = Math.toRadians(15.0)

            val layer2 = SpriteLayer(dirt)
            layer2.pos = vec(32, 32)
            layer2.rotation = Math.toRadians(-15.0)
            layer.add(layer2)

            layer.BUS.hook<GuiLayerEvents.MouseMove> {
                layer.sprite = if(layer.mouseOver) stone else dirt
            }
            layer2.BUS.hook<GuiLayerEvents.MouseMove> {
                layer2.sprite = if(layer2.mouseOver) stone else dirt
            }
            screen.facade.root.add(layer)
        }

        +FacadeScreenConfig("scheduled_callbacks", "Scheduled Callbacks") { screen ->
            val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16).getSprite("")
            val stone = Mosaic("minecraft:textures/block/stone.png".toRl(), 16, 16).getSprite("")
            val layer = SpriteLayer(dirt)
            layer.pos = vec(32, 32)

            layer.BUS.hook<GuiLayerEvents.MouseDown> {
                if(layer.mouseOver && layer.sprite == dirt) {
                    layer.sprite = stone
                    layer.delay(20f) {
                        layer.sprite = dirt
                    }
                }
            }
            screen.facade.root.add(layer)
        }

        +FacadeScreenConfig("animations", "Animations") { screen ->
            val dirt = Mosaic("minecraft:textures/block/dirt.png".toRl(), 16, 16).getSprite("")
            val layer = SpriteLayer(dirt)
            val from = vec(32, 32)
            val to = vec(64, 64)
            layer.pos = from

            layer.BUS.hook<GuiLayerEvents.MouseDown> {
                if(layer.mouseOver) {
                    layer.pos_rm.animate(from, to, 20f, Easing.easeOutQuad, 10f)
                        .onComplete {
                            layer.pos_rm.animate(to, from, 20f, Easing.easeInCubic)
                        }
                }
            }
            screen.facade.root.add(layer)
        }

        +FacadeScreenConfig("simple_text", "Simple Text", ::SimpleTextTestScreen)
        +FacadeScreenConfig("clip_to_bounds", "Clip to Bounds", ::ClipToBoundsTestScreen)
        +FacadeScreenConfig("masking", "Masking", ::MaskingTestScreen)
        +FacadeScreenConfig("opacity", "Opacity", ::OpacityTestScreen)
        +FacadeScreenConfig("render_fbo_scale", "Render to FBO Scale", ::RenderFBOScaleTest)
        +FacadeScreenConfig("render_quad_scale", "Render to Quad Scale", ::RenderQuadScaleTest)
        +FacadeScreenConfig("simple_yoga", "Yoga Simple Flex", ::SimpleYogaScreen)
        +FacadeScreenConfig("yoga_list", "Yoga List", ::YogaListScreen)
        +FacadeScreenConfig("pastry", "Pastry", ::PastryTestScreen)

        +UnitTestSuite("rmvalue") {
            add<RMValueTests>()
        }
    }

    fun FacadeScreenConfig(id: String, name: String, block: (FacadeScreen) -> Unit): TestScreenConfig {
        return TestScreenConfig(id, name, itemGroup) {
            customScreen {
                try {
                    val screen = FacadeScreen(StringTextComponent(name))
                    block(screen)
                    screen
                } catch (e: Exception) {
                    logger.error("Error in GUI:", e)
                    SafetyNetErrorScreen(e)
                }
            }
        }
    }

    fun FacadeScreenConfig(id: String, name: String, block: () -> FacadeTestScreen): TestScreenConfig {
        return TestScreenConfig(id, name, itemGroup) {
            customScreen {
                try {
                    block()
                } catch (e: Exception) {
                    logger.error("Error in GUI:", e)
                    SafetyNetErrorScreen(e)
                }
            }
        }
    }
}



internal val logger = LogManager.getLogger("LibrarianLib: Facade Test")
