package com.teamwizardry.librarianlib.facade.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import com.teamwizardry.librarianlib.facade.container.FacadeControllerRegistry
import com.teamwizardry.librarianlib.facade.container.FacadeControllerType
import com.teamwizardry.librarianlib.facade.container.FacadeViewRegistry
import com.teamwizardry.librarianlib.facade.example.ExampleAlignmentScreen
import com.teamwizardry.librarianlib.facade.example.FacadeExampleMod
import com.teamwizardry.librarianlib.facade.example.GuessingGameScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.AllTheSquaresScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.HelloSquareScreen
import com.teamwizardry.librarianlib.facade.example.gettingstarted.SquaresAllTheWayDownScreen
import com.teamwizardry.librarianlib.facade.example.transform.PositionExampleScreen
import com.teamwizardry.librarianlib.facade.example.transform.VisualizationTestScreen
import com.teamwizardry.librarianlib.facade.test.controllers.*
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerBlockEntity
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerSelectorController
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerSelectorView
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerSet
import com.teamwizardry.librarianlib.facade.test.screens.*
import com.teamwizardry.librarianlib.facade.test.screens.pastry.*
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestBlock
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

internal object LibLibFacadeTest {
    val logManager: ModLogManager = ModLogManager("liblib-facade-test", "LibrarianLib Facade Test")
    val manager: TestModContentManager = TestModContentManager("liblib-facade-test", "Facade", logManager)

    lateinit var simpleControllerType: FacadeControllerType<SimpleController>
    lateinit var simpleInventoryControllerType: FacadeControllerType<SimpleInventoryController>
    lateinit var testControllerSelectorControllerType: FacadeControllerType<TestControllerSelectorController>

    lateinit var basicControllers: TestControllerSet

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            CourierServerPlayNetworking.registerGlobalReceiver(SyncSelectionPacket.type) { packet, context ->
                context.execute {
                    context.player.mainHandStack.orCreateNbt.put("saved_selection", NbtString.of(packet.id))
                }
            }

            simpleControllerType = FacadeControllerRegistry.register(
                Identifier("liblib-facade-test:simple_controller"),
                SimpleController::class.java
            )
            simpleInventoryControllerType = FacadeControllerRegistry.register(
                Identifier("liblib-facade-test:simple_inventory_controller"),
                SimpleInventoryController::class.java
            )
            testControllerSelectorControllerType = FacadeControllerRegistry.register(
                Identifier("liblib-facade-test:test_controller_selector"),
                TestControllerSelectorController::class.java
            )

            manager.create<TestItem>("basics") { name = "Basics" }
            manager.create<TestItem>("layers") { name = "Layers" }
            manager.create<TestItem>("animations") { name = "Animations/Time" }
            manager.create<TestItem>("clipping_compositing") { name = "Clipping/Compositing" }
            manager.create<TestItem>("advanced") { name = "Advanced" }
            manager.create<TestItem>("examples") { name = "Examples" }

            manager.create<TestItem>("simple_controller") {
                name = "Simple controller"
                rightClick.server {
                    simpleControllerType.open(player as ServerPlayerEntity, Text.of("Simple Controller"))
                }
            }
            manager.create<TestBlock>("simple_inventory") {
                name = "Simple inventory"
                blockEntity(::SimpleInventoryBlockEntity)
                rightClick.server {
                    simpleInventoryControllerType.open(player as ServerPlayerEntity, Text.of("Simple Inventory"), pos)
                }
            }

            basicControllers = TestControllerSet("basic_controllers") {
                controller("single_slot", SingleSlotController::class.java)
                controller("slot_occlusion", OcclusionController::class.java)
            }
            manager.create<TestBlock>("basic_controllers") {
                name = "Basic Controllers"
                blockEntity<TestControllerBlockEntity> { type, pos, state ->
                    TestControllerBlockEntity(type, pos, state, basicControllers)
                }
                blockEntityTickFunction(TestControllerBlockEntity::tick)

                rightClick.server {
                    testControllerSelectorControllerType.open(player as ServerPlayerEntity, Text.of("Basic Controllers"), pos)
                }
            }

            manager.registerCommon()

            FacadeExampleMod.CommonInit.init()
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
            FacadeViewRegistry.register(simpleControllerType, ::SimpleView)
            FacadeViewRegistry.register(simpleInventoryControllerType, ::SimpleInventoryView)
            FacadeViewRegistry.register(testControllerSelectorControllerType, ::TestControllerSelectorView)
            FacadeViewRegistry.register(basicControllers.controllerType(SingleSlotController::class.java), ::SingleSlotView)
            FacadeViewRegistry.register(basicControllers.controllerType(OcclusionController::class.java), ::OcclusionView)
            manager.registerClient()

            FacadeExampleMod.ClientInit.clientInit()
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
                        val saved = stack.nbt?.getString("saved_selection") ?: ""
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
