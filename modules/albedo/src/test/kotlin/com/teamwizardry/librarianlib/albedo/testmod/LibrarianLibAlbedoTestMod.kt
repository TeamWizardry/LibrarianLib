package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.testmod.shaders.FloatUniform
import com.teamwizardry.librarianlib.albedo.testmod.shaders.PrimitiveUniform
import com.teamwizardry.librarianlib.albedo.testmod.shaders.SimpleFrag
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-albedo-test")
object LibrarianLibAlbedoTestMod: TestMod("albedo", "Albedo", logger) {
    init {
        +test("simple_frag", "Simple Fragment Shader", "A simple fragment shader with no uniforms") {
            SimpleFrag
        }

        +test("primitive_uniform", "Simple Primitive Uniform", "A simple time-based color fragment") {
            PrimitiveUniform
        }

        +test("float_uniform", "Float Uniform", "Simple float uniform tests") {
            FloatUniform
        }
    }

    inline fun test(id: String, name: String, description: String, crossinline shader: () -> ShaderTest): TestScreenConfig {
        var crashed = false
        return TestScreenConfig(id, name) {
            this.description = description
            size = vec(128, 128)
            lazyConfig {
                client {

                    draw {
                        if(crashed) return@draw
                        try {
                            shader().draw()
                        } catch(e: Exception) {
                            logger.error("", e)
                            crashed = true
                        }
                    }

                    onClose {
                        crashed = false
                        shader().delete()
                    }
                }
            }
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Albedo Test")
