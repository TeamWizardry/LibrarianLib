package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.LibrarianLibAlbedoModule
import com.teamwizardry.librarianlib.albedo.testmod.shaders.*
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-albedo-test")
object LibrarianLibAlbedoTestMod: TestMod(LibrarianLibAlbedoModule) {
    init {
        +test("simple_frag", "Simple Fragment Shader", "A simple fragment shader with no uniforms") {
            SimpleFrag
        }

        +test("primitive_uniform", "Simple Primitive Uniform", "A simple time-based color fragment") {
            PrimitiveUniform
        }

        +test("float_uniform", "Float Uniform", "float uniform tests") {
            FloatUniform
        }

        +test("float_array_uniform", "Float Array Uniform", "float array uniform tests") {
            FloatArrayUniform
        }

        +test("int_uniform", "Int Uniform", "int uniform tests") {
            IntUniform
        }

        +test("int_array_uniform", "Int Array Uniform", "int array uniform tests") {
            IntArrayUniform
        }

        +test("bool_uniform", "Bool Uniform", "bool uniform tests") {
            BoolUniform
        }

        +test("bool_array_uniform", "Bool Array Uniform", "bool array uniform tests") {
            BoolArrayUniform
        }

        +test("matrix_uniform", "Matrix Uniform", "Matrix uniform tests") {
            MatrixUniform
        }

        +test("matrix_array_uniform", "Matrix Array Uniform", "Matrix array uniform tests") {
            MatrixArrayUniform
        }

        +test("sampler_uniform", "Sampler Uniform", "Sampler uniform tests") {
            SamplerUniform
        }

        +test("sampler_array_uniform", "Sampler Array Uniform", "Sampler array uniform tests") {
            SamplerArrayUniform
        }

        +test("struct_uniform", "Struct Uniform", "Struct uniform tests") {
            StructUniform
        }
    }

    private inline fun test(id: String, name: String, description: String, crossinline shader: () -> ShaderTest<*>): TestScreenConfig {
        var crashed = false
        return TestScreenConfig(id, name) {
            this.description = description
            size = vec(128, 128)
            lazyConfig {
                client {

                    draw {
                        if(crashed) return@draw
                        try {
                            shader().draw(matrix)
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

internal val logger = LibrarianLibAlbedoTestMod.makeLogger(null)
