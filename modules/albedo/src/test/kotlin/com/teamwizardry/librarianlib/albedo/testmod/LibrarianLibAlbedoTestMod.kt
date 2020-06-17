package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.testmod.shaders.*
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

        +test("float_array_uniform", "Float Array Uniform", "Simple float array uniform tests") {
            FloatArrayUniform
        }

        +test("int_uniform", "Int Uniform", "Simple int uniform tests") {
            IntUniform
        }

        +test("int_array_uniform", "Int Array Uniform", "Simple int array uniform tests") {
            IntArrayUniform
        }

        +test("bool_uniform", "Bool Uniform", "Simple bool uniform tests") {
            BoolUniform
        }

        +test("bool_array_uniform", "Bool Array Uniform", "Simple bool array uniform tests") {
            BoolArrayUniform
        }

        +test("matrix_uniform", "Matrix Uniform", "Simple matrix uniform tests") {
            MatrixUniform
        }

        +test("matrix_array_uniform", "Matrix Array Uniform", "Simple matrix array uniform tests") {
            MatrixArrayUniform
        }

        +test("sampler_uniform", "Sampler Uniform", "Simple sampler uniform tests") {
            SamplerUniform
        }

        +test("sampler_array_uniform", "Sampler Array Uniform", "Simple sampler array uniform tests") {
            SamplerArrayUniform
        }
    }

    inline fun test(id: String, name: String, description: String, crossinline shader: () -> ShaderTest<*>): TestScreenConfig {
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
                        } catch(e: ExceptionInInitializerError) {
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
