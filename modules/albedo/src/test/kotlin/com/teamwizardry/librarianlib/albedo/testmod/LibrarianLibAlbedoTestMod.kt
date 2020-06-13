package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.testmod.shaders.SimpleFrag
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-albedo-test")
object LibrarianLibAlbedoTestMod: TestMod("albedo", "Albedo", logger) {
    init {
        +TestScreenConfig("simple_frag", "Simple Fragment Shader") {
            description = "A simple fragment shader with no uniforms"
            size = vec(128, 128)
            lazyConfig {
                client {
                    draw {
                        SimpleFrag.draw()
                    }
                }
            }
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Albedo Test")
