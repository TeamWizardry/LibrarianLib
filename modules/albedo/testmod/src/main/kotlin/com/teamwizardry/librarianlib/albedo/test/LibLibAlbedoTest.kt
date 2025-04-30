package com.teamwizardry.librarianlib.albedo.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.test.renderers.FlatColorTestRenderer
import com.teamwizardry.librarianlib.albedo.test.renderers.FlatLinesTestRenderer
import com.teamwizardry.librarianlib.albedo.test.renderers.ShadedTextureTestRenderer
import com.teamwizardry.librarianlib.albedo.test.shaders.*
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
import com.teamwizardry.librarianlib.testcore.module.TestModule
import com.teamwizardry.librarianlib.testcore.module.TestModuleClient
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon
import org.lwjgl.glfw.GLFW

internal object LibLibAlbedoTest : TestModule("albedo", "Albedo"){

    @AutoService(TestModuleCommon::class)
    class CommonInit : TestModuleCommon {
        override val module = LibLibAlbedoTest
        val logger = logManager.makeLogger<CommonInit>()

        override fun initializeCommon(config: TestModuleConfig) {
            test(config, "simple_renderbuffer", "Simple RenderBuffer", "A simple flat color RenderBuffer")
            test(config, "base_flat_color", "FlatColorRenderBuffer", "The built-in flat color buffer")
            test(config, "base_flat_texture", "FlatTextureRenderBuffer", "The built-in flat texture buffer")
            test(config, "base_flat_lines", "FlatLinesRenderBuffer", "The built-in flat lines buffer")
            test(config, "flat_line_bevels", "Flat line bevels", "Testing line beveling")

            test(config, "simple_frag", "Simple Fragment Shader", "A simple fragment shader with no uniforms")
            test(config, "primitive_uniform", "Simple Primitive Uniform", "A simple time-based color fragment")
            test(config, "float_uniform", "Float Uniform", "float uniform tests")
            test(config, "float_array_uniform", "Float Array Uniform", "float array uniform tests")
            test(config, "int_uniform", "Int Uniform", "int uniform tests")
            test(config, "int_array_uniform", "Int Array Uniform", "int array uniform tests")
            test(config, "bool_uniform", "Bool Uniform", "bool uniform tests")
            test(config, "bool_array_uniform", "Bool Array Uniform", "bool array uniform tests")
            test(config, "matrix_uniform", "Matrix Uniform", "Matrix uniform tests")
            test(config, "matrix_array_uniform", "Matrix Array Uniform", "Matrix array uniform tests")
            test(config, "sampler_uniform", "Sampler Uniform", "Sampler uniform tests")
            test(config, "sampler_array_uniform", "Sampler Array Uniform", "Sampler array uniform tests")
            test(config, "struct_uniform", "Struct Uniform", "Struct uniform tests")

            testEntity(config, "world_flat_color", "World Flat Color", "The built-in flat color buffer in world space")
            testEntity(config, "world_flat_lines", "World Flat Lines", "The built-in flat lines buffer in world space")
            testEntity(config, "world_shaded", "World Shaded", "The built-in shaded render buffer")
        }

        private fun test(config: TestModuleConfig, id: String, name: String, description: String) {
            config.item(id) {
                this.name = name
                this.description = description
            }
        }

        private fun testEntity(config: TestModuleConfig, id: String, name: String, description: String) {
            config.entity(id) {
                this.name = name
                this.description = description
            }
        }
    }

    @AutoService(TestModuleClient::class)
    class ClientInit : TestModuleClient {
        override val module = LibLibAlbedoTest
        val logger = logManager.makeLogger<ClientInit>()

        override fun initializeClient(config: TestModuleConfig) {
            test(config, "simple_renderbuffer", SimpleRenderBuffer)
            test(config, "base_flat_color", TestFlatColorRenderBuffer)
            test(config, "base_flat_texture", TestFlatTextureRenderBuffer)
            test(config, "base_flat_lines", TestFlatLinesRenderBuffer)
            test(config, "flat_line_bevels", TestFlatLineBevels)

//            test(config, "simple_frag", )
//            test(config, "primitive_uniform", )
            test(config, "float_uniform", TestFloatUniform)
            test(config, "float_array_uniform", TestFloatArrayUniform)
            test(config, "int_uniform", TestIntUniform)
            test(config, "int_array_uniform", TestIntArrayUniform)
            test(config, "bool_uniform", TestBoolUniform)
            test(config, "bool_array_uniform", TestBoolArrayUniform)
//            test(config, "matrix_uniform", TestMatrixUniform)
//            test(config, "matrix_array_uniform", TestMatrixArrayUniform)
//            test(config, "sampler_uniform", TestSamplerUniform)
//            test(config, "sampler_array_uniform", TestSamplerArrayUniform)
//            test(config, "struct_uniform", TestStructUniform)

            testEntity(config, "world_flat_color", FlatColorTestRenderer)
            testEntity(config, "world_flat_lines", FlatLinesTestRenderer)
            testEntity(config, "world_shaded", ShadedTextureTestRenderer)

            AlbedoTestRenderManager.registerEvents()
        }

        private fun test(config: TestModuleConfig, id: String, shader: ShaderTest) {
            var crashed = false
            config.item(id) {
                rightClick.client {
                    Client.openScreen(TestScreen {
                        size = vec(shader.width, shader.height)

                        draw {
                            if(crashed) return@draw
                            try {
                                shader.draw(matrix, mousePos)
                            } catch(e: Exception) {
                                logger.error("", e)
                                crashed = true
                            } catch(e: ExceptionInInitializerError) {
                                logger.error("", e)
                                crashed = true
                            }
                        }

                        var f3 = false
                        keyPressed {
                            if (key == GLFW.GLFW_KEY_F3) {
                                f3 = true
                            } else if (f3 && key == GLFW.GLFW_KEY_T) {
                                try {
                                    Shader.reloadShaders()
                                } catch(e: Exception) {
                                    logger.error("", e)
                                    crashed = true
                                } catch(e: ExceptionInInitializerError) {
                                    logger.error("", e)
                                    crashed = true
                                }
                            }
                        }
                        keyReleased {
                            if(key == GLFW.GLFW_KEY_F3) {
                                f3 = false
                            }
                        }

                        onClose {
                            crashed = false
                            shader.destroy()
                        }
                    })
                }
            }
        }

        private fun testEntity(config: TestModuleConfig, id: String, renderer: AlbedoTestRenderer) {
            config.entity(id) {
                AlbedoTestRenderManager.worldRenderers[this.id] = renderer
                rightClick.client {
                    renderer.crashed = false
                }
            }
        }
    }
}
