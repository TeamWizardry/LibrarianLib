@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.FacadeContainerType
import com.teamwizardry.librarianlib.facade.example.*
import com.teamwizardry.librarianlib.facade.example.gettingstarted.*
import com.teamwizardry.librarianlib.facade.example.transform.*
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleContainerScreen
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleInventoryContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleInventoryContainerScreen
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleInventoryTile
import com.teamwizardry.librarianlib.facade.testmod.screens.*
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestScreen
import com.teamwizardry.librarianlib.facade.testmod.value.RMValueTests
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestBlockConfig
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod("librarianlib-facade-test")
object LibrarianLibFacadeTestMod: TestMod(LibrarianLibFacadeModule) {
    val groups: Map<String, FacadeTestGroup> = mapOf(
        "basics" to FacadeTestGroup("Basics", listOf(
            FacadeTest("Empty", ::EmptyTestScreen),
            FacadeTest("zIndex", ::ZIndexTestScreen),
            FacadeTest("Layer Transform", ::LayerTransformTestScreen),
            FacadeTest("Layer MouseOver/MouseOff", ::LayerMouseOverOffTestScreen),
            FacadeTest("Event Priority", ::EventPriorityScreen),
        )),
        "layers" to FacadeTestGroup("Layers", listOf(
            FacadeTest("Simple Sprite", ::SimpleSpriteTestScreen),
            FacadeTest("Simple Text", ::SimpleTextTestScreen),
            FacadeTest("DragLayer", ::DragLayerTestScreen)
        )),
        "animations" to FacadeTestGroup("Animations/Time", listOf(
            FacadeTest("Animations", ::AnimationTestScreen),
            FacadeTest("Scheduled Callbacks", ::ScheduledCallbacksTestScreen),
            FacadeTest("Scheduled Repeated Callbacks", ::ScheduledRepeatedCallbacksTestScreen)
        )),
        "clipping_compositing" to FacadeTestGroup("Clipping/Compositing", listOf(
            FacadeTest("Clip to Bounds", ::ClipToBoundsTestScreen),
            FacadeTest("Masking", ::MaskingTestScreen),
            FacadeTest("Opacity", ::OpacityTestScreen),
            FacadeTest("Blending", ::BlendingTestScreen),
            FacadeTest("Render to FBO Scale", ::RenderFBOScaleTest),
            FacadeTest("Render to Quad Scale", ::RenderQuadScaleTest)
        )),
        "advanced" to FacadeTestGroup("Advanced", listOf(
            FacadeTest("Yoga Simple Flex", ::SimpleYogaScreen),
            FacadeTest("Yoga List", ::YogaListScreen),
            FacadeTest("Pastry", ::PastryTestScreen)
        )),
        "examples" to FacadeTestGroup("Examples", listOf(
            FacadeTest("Alignment", ::ExampleAlignmentScreen),
            FacadeTest("Getting Started > Hello Square", ::HelloSquareScreen),
            FacadeTest("Getting Started > All the Squares", ::AllTheSquaresScreen),
            FacadeTest("Getting Started > Squares all the Way Down", ::SquaresAllTheWayDownScreen),
            FacadeTest("Guessing Game", ::GuessingGameScreen),
            FacadeTestGroup("Transform >", listOf(
                FacadeTest("Visualization Test", ::VisualizationTestScreen),
                FacadeTest("Position", ::PositionExampleScreen),
            )),
        )),
    )

    val simpleContainerType: FacadeContainerType<SimpleContainer>
    val simpleInventoryContainerType: FacadeContainerType<SimpleInventoryContainer>

    init {
        groups.forEach { (id, group) ->
            +TestScreenConfig(id, group.name, itemGroup) {
                customScreen {
                    TestListScreen(group.name, group.tests)
                }
            }
        }

        simpleInventoryContainerType = FacadeContainerType(SimpleInventoryContainer::class.java)
        simpleInventoryContainerType.registryName = loc("librarianlib-facade-test", "simple_inventory")
        +TestBlock(TestBlockConfig("simple_inventory", "Simple Inventory") {
            tile(::SimpleInventoryTile)

            rightClick.server {
                simpleInventoryContainerType.open(player as ServerPlayerEntity, StringTextComponent("Simple Inventory"), pos)
            }
        })

        simpleContainerType = FacadeContainerType(SimpleContainer::class.java)
        simpleContainerType.registryName = loc("librarianlib-facade-test", "simple_container")
        +TestItem(TestItemConfig("simple_container", "Simple Container") {
            rightClick.server {
                simpleContainerType.open(player as ServerPlayerEntity, StringTextComponent("Simple Container"))
            }
        })

        +UnitTestSuite("rmvalue") {
            add<RMValueTests>()
        }
    }

    @SubscribeEvent
    fun registerContainers(e: RegistryEvent.Register<ContainerType<*>>) {
        e.registry.register(simpleContainerType)
        e.registry.register(simpleInventoryContainerType)
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        ScreenManager.registerFactory(simpleContainerType, ::SimpleContainerScreen)
        ScreenManager.registerFactory(simpleInventoryContainerType, ::SimpleInventoryContainerScreen)
    }
}

internal val logger = LibrarianLibFacadeTestMod.makeLogger(null)
