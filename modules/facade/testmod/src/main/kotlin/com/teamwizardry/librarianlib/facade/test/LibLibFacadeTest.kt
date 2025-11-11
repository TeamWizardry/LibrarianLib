package com.teamwizardry.librarianlib.facade.test

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.example.ExampleAlignmentScreen
import com.teamwizardry.librarianlib.facade.example.GuessingGameScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.AllTheSquaresScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.HelloSquareScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.SquaresAllTheWayDownScreen
import com.teamwizardry.librarianlib.facade.example.transform.PositionExampleScreen
import com.teamwizardry.librarianlib.facade.example.transform.VisualizationTestScreen
import com.teamwizardry.librarianlib.facade.test.screens.*
import com.teamwizardry.librarianlib.facade.test.screens.pastry.*
import com.teamwizardry.librarianlib.facade.test.screens.textinput.*
import com.teamwizardry.librarianlib.testcore.content.TestModuleConfig
import com.teamwizardry.librarianlib.testcore.content.utils.TestScreen
import com.teamwizardry.librarianlib.testcore.module.TestModule
import com.teamwizardry.librarianlib.testcore.module.TestModuleClient
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon


internal object LibLibFacadeTest : TestModule("facade", "Facade") {
    @AutoService(TestModuleCommon::class)
    class CommonInit : TestModuleCommon {
        override val module = LibLibFacadeTest
        val logger = logManager.makeLogger<CommonInit>()

        override fun initializeCommon(config: TestModuleConfig) {
            config.item("basics") { name = "Basics" }
            config.item("layers") { name = "Layers" }
            config.item("animations") { name = "Animations/Time" }
            config.item("clipping_compositing") { name = "Clipping/Compositing" }
            config.item("advanced") { name = "Advanced" }
            config.item("text_input") { name = "Text Input" }
            config.item("examples") { name = "Examples" }
        }
    }

    @AutoService(TestModuleClient::class)
    class ClientInit : TestModuleClient {
        override val module = LibLibFacadeTest
        val logger = logManager.makeLogger<ClientInit>()

        override fun initializeClient(config: TestModuleConfig) {

            fun setupTests(name: String, callback: TestSelectorBuilder.() -> Unit) {
                val builder = TestSelectorBuilder()
                builder.callback()
                val selector = builder.build()

                config.item(name) {
                    var savedSelection = ""
                    rightClick.client {
                        sneaking {
                            Client.openScreen(TestSelectorScreen(name, selector) { savedSelection = it })
                        }
                        notSneaking {
                            val screen = selector.index[savedSelection]?.create()
                            if(screen != null) {
                                Client.openScreen(screen)
                            } else {
                                Client.openScreen(TestSelectorScreen(name, selector) { savedSelection = it })
                            }
                        }
                    }
                }
            }

            setupTests("basics") {
                screen("Empty", ::EmptyTestScreen)
                screen("zIndex", ::ZIndexTestScreen)
                screen("Layer Transform", ::LayerTransformTestScreen)
                screen("Layer MouseOver/MouseOff", ::LayerMouseOverOffTestScreen)
                screen("Event Priority", ::EventPriorityScreen)
            }
            setupTests("layers") {
                screen("Simple Sprite", ::SimpleSpriteTestScreen)
                screen("Simple Text", ::SimpleTextTestScreen)
                screen("Text Embeds", ::TextEmbedsTestScreen)
                screen("DragLayer", ::DragLayerTestScreen)
            }
            setupTests("animations") {
                screen("Animations", ::AnimationTestScreen)
                screen("Scheduled Callbacks", ::ScheduledCallbacksTestScreen)
                screen("Scheduled Repeated Callbacks", ::ScheduledRepeatedCallbacksTestScreen)
            }
            setupTests("clipping_compositing") {
                screen("Clip to Bounds", ::ClipToBoundsTestScreen)
                screen("Masking", ::MaskingTestScreen)
                screen("Opacity", ::OpacityTestScreen)
                screen("Blending", ::BlendingTestScreen)
                screen("Render to FBO Scale", ::RenderFBOScaleTest)
                screen("Render to Quad Scale", ::RenderQuadScaleTest)
            }
            setupTests("advanced") {
                screen("Pastry", ::PastryTestScreen)
                screen("Rect2dUnion", ::Rect2dUnionTestScreen)
            }
            setupTests("text_input") {
                screen("Simple text input", ::SimpleTextInputTestScreen)
                screen("Multi-container text input", ::MultiContainerTextInputTestScreen)
            }
            setupTests("examples") {
                screen("Alignment", ::ExampleAlignmentScreen)

                group("Tutorial") {
                    group("Getting Started") {
                        screen("Hello Square") { HelloSquareScreen() }
                        screen("All the Squares") { AllTheSquaresScreen() }
                        screen("Squares all the Way Down") { SquaresAllTheWayDownScreen() }
                    }
                    screen("Guessing Game") { GuessingGameScreen() }
                }

                group("Transform >") {
                    screen("Visualization Test", ::VisualizationTestScreen)
                    screen("Position", ::PositionExampleScreen)
                }
            }
        }

    }
}
