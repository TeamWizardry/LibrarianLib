package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.albedo.test.shaders.*
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibAlbedoTest {
    val logManager: ModLogManager = ModLogManager("liblib-albedo-test", "LibrarianLib Albedo Test")
    val manager: TestModContentManager = TestModContentManager("liblib-albedo-test", "Albedo", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            test("simple_renderbuffer", "Simple RenderBuffer", "A simple flat color RenderBuffer")
            test("base_flat_color", "FlatColorRenderBuffer", "The built-in flat color buffer")
            test("base_flat_texture", "FlatTextureRenderBuffer", "The built-in flat texture buffer")

            test("simple_frag", "Simple Fragment Shader", "A simple fragment shader with no uniforms")
            test("primitive_uniform", "Simple Primitive Uniform", "A simple time-based color fragment")
            test("float_uniform", "Float Uniform", "float uniform tests")
            test("float_array_uniform", "Float Array Uniform", "float array uniform tests")
            test("int_uniform", "Int Uniform", "int uniform tests")
            test("int_array_uniform", "Int Array Uniform", "int array uniform tests")
            test("bool_uniform", "Bool Uniform", "bool uniform tests")
            test("bool_array_uniform", "Bool Array Uniform", "bool array uniform tests")
            test("matrix_uniform", "Matrix Uniform", "Matrix uniform tests")
            test("matrix_array_uniform", "Matrix Array Uniform", "Matrix array uniform tests")
            test("sampler_uniform", "Sampler Uniform", "Sampler uniform tests")
            test("sampler_array_uniform", "Sampler Array Uniform", "Sampler array uniform tests")
            test("struct_uniform", "Struct Uniform", "Struct uniform tests")
            manager.registerCommon()
        }

        private fun test(id: String, name: String, description: String) {
            manager.create<TestItem>(id) {
                this.name = name
                this.description = description
            }
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            test("simple_renderbuffer", SimpleRenderBuffer)
            test("base_flat_color", TestFlatColorRenderBuffer)
            test("base_flat_texture", TestFlatTextureRenderBuffer)

            manager.registerClient()
        }

        private fun test(id: String, shader: ShaderTest) {
            var crashed = false
            manager.named<TestItem>(id) {
                rightClick.client {
                    Client.openScreen(TestScreen {
                        size = vec(128, 128)

                        draw {
                            if(crashed) return@draw
                            try {
                                shader.draw(matrix)
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
                            shader.destroy()
                        }
                    })
                }
            }
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
