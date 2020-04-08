@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.gui.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.gui.FacadeScreen
import com.teamwizardry.librarianlib.gui.layers.SpriteComponent
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.sprites.Texture
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-gui-test")
object LibrarianLibSpritesTestMod: TestMod("gui", "Gui", logger) {
    init {
        +FacadeScreenConfig("empty", "Empty") {

        }

        +FacadeScreenConfig("sprite", "Simple Sprite") { screen ->
            val dirt = Texture("minecraft:textures/block/dirt.png".toRl(), 16, 16)
            val layer = SpriteComponent(dirt.getSprite(""))
            screen.facade.root.add(layer)
        }

        +FacadeScreenConfig("layer_transform", "Layer Transform") { screen ->
            val dirt = Texture("minecraft:textures/block/dirt.png".toRl(), 16, 16)
            val layer = SpriteComponent(dirt.getSprite(""))
            layer.pos = vec(32, 32)
            layer.rotation = Math.toRadians(15.0)

            val layer2 = SpriteComponent(dirt.getSprite(""))
            layer2.pos = vec(32, 32)
            layer2.rotation = Math.toRadians(-15.0)
            layer.add(layer2)
            screen.facade.root.add(layer)
        }
    }

    fun FacadeScreenConfig(id: String, name: String, block: (FacadeScreen) -> Unit): TestScreenConfig {
        return TestScreenConfig(id, name, itemGroup) {
            customScreen {
                val screen = FacadeScreen(StringTextComponent(name))
                block(screen)
                screen
            }
        }
    }
}



internal val logger = LogManager.getLogger("LibrarianLib: Gui Test")
