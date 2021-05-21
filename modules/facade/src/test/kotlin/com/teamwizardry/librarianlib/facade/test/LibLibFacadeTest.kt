package com.teamwizardry.librarianlib.facade.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.courier.CourierPacketType
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import com.teamwizardry.librarianlib.facade.example.ExampleAlignmentScreen
import com.teamwizardry.librarianlib.facade.example.GuessingGameScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.AllTheSquaresScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.HelloSquareScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.SquaresAllTheWayDownScreen
import com.teamwizardry.librarianlib.facade.example.transform.PositionExampleScreen
import com.teamwizardry.librarianlib.facade.example.transform.VisualizationTestScreen
import com.teamwizardry.librarianlib.facade.test.screens.*
import com.teamwizardry.librarianlib.facade.test.screens.pastry.*
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.nbt.StringTag
import net.minecraft.util.Identifier

internal object LibLibFacadeTest {
    val logManager: ModLogManager = ModLogManager("liblib-facade-test", "LibrarianLib Facade Test")
    val manager: TestModContentManager = TestModContentManager("liblib-facade-test", "Facade", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            CourierServerPlayNetworking.registerGlobalReceiver(SyncSelectionPacket.type) { packet, context ->
                context.execute {
                    context.player.mainHandStack.orCreateTag.put("saved_selection", StringTag.of(packet.id))
                }
            }

            manager.create<TestItem>("basics") { name = "Basics" }
            manager.create<TestItem>("layers") { name = "Layers" }
            manager.create<TestItem>("animations") { name = "Animations/Time" }
            manager.create<TestItem>("clipping_compositing") { name = "Clipping/Compositing" }
            manager.create<TestItem>("advanced") { name = "Advanced" }
            manager.create<TestItem>("examples") { name = "Examples" }

            manager.registerCommon()
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
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
            manager.registerClient()
        }

        private fun setupTests(name: String, config: TestSelectorBuilder.() -> Unit) {
            val builder = TestSelectorBuilder()
            builder.config()
            val selector = builder.build()

            manager.named<TestItem>(name) {
                rightClick.client {
                    sneaking {
                        Client.openScreen(TestSelectorScreen(name, selector))
                    }
                    notSneaking {
                        val saved = stack.tag?.getString("saved_selection") ?: ""
                        val screen = selector.index[saved]?.create()
                        if(screen != null) {
                            Client.openScreen(screen)
                        } else {
                            Client.openScreen(TestSelectorScreen(name, selector))
                        }
                    }
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
