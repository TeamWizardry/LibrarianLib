@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.FacadeContainerType
import com.teamwizardry.librarianlib.facade.example.*
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterItem
import com.teamwizardry.librarianlib.facade.example.gettingstarted.*
import com.teamwizardry.librarianlib.facade.example.transform.*
import com.teamwizardry.librarianlib.facade.testmod.containers.*
import com.teamwizardry.librarianlib.facade.testmod.containers.base.*
import com.teamwizardry.librarianlib.facade.testmod.screens.*
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestScreen
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.Rect2dUnionTestScreen
import com.teamwizardry.librarianlib.facade.testmod.value.RMValueTests
import com.teamwizardry.librarianlib.testcore.TestMod
import com.teamwizardry.librarianlib.testcore.objects.TestBlock
import com.teamwizardry.librarianlib.testcore.objects.TestBlockConfig
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import com.teamwizardry.librarianlib.testcore.objects.TestScreenConfig
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod("ll-facade-test")
object LibrarianLibFacadeTestMod : TestMod(LibrarianLibFacadeModule) {
    val groups: Map<String, FacadeTestGroup> = mapOf(
        "basics" to FacadeTestGroup(
            "Basics", listOf(
                FacadeTest("Empty") { ScreenConstructor(::EmptyTestScreen) },
                FacadeTest("zIndex") { ScreenConstructor(::ZIndexTestScreen) },
                FacadeTest("Layer Transform") { ScreenConstructor(::LayerTransformTestScreen) },
                FacadeTest("Layer MouseOver/MouseOff") { ScreenConstructor(::LayerMouseOverOffTestScreen) },
                FacadeTest("Event Priority") { ScreenConstructor(::EventPriorityScreen) },
            )
        ),
        "layers" to FacadeTestGroup(
            "Layers", listOf(
                FacadeTest("Simple Sprite") { ScreenConstructor(::SimpleSpriteTestScreen) },
                FacadeTest("Simple Text") { ScreenConstructor(::SimpleTextTestScreen) },
                FacadeTest("Text Embeds") { ScreenConstructor(::TextEmbedsTestScreen) },
                FacadeTest("DragLayer") { ScreenConstructor(::DragLayerTestScreen) },
            )
        ),
        "animations" to FacadeTestGroup(
            "Animations/Time", listOf(
                FacadeTest("Animations") { ScreenConstructor(::AnimationTestScreen) },
                FacadeTest("Scheduled Callbacks") { ScreenConstructor(::ScheduledCallbacksTestScreen) },
                FacadeTest("Scheduled Repeated Callbacks") { ScreenConstructor(::ScheduledRepeatedCallbacksTestScreen) },
            )
        ),
        "clipping_compositing" to FacadeTestGroup(
            "Clipping/Compositing", listOf(
                FacadeTest("Clip to Bounds") { ScreenConstructor(::ClipToBoundsTestScreen) },
                FacadeTest("Masking") { ScreenConstructor(::MaskingTestScreen) },
                FacadeTest("Opacity") { ScreenConstructor(::OpacityTestScreen) },
                FacadeTest("Blending") { ScreenConstructor(::BlendingTestScreen) },
                FacadeTest("Render to FBO Scale") { ScreenConstructor(::RenderFBOScaleTest) },
                FacadeTest("Render to Quad Scale") { ScreenConstructor(::RenderQuadScaleTest) },
            )
        ),
        "advanced" to FacadeTestGroup(
            "Advanced", listOf(
                FacadeTest("Pastry") { ScreenConstructor(::PastryTestScreen) },
                FacadeTest("Rect2dUnion") { ScreenConstructor(::Rect2dUnionTestScreen) },
            )
        ),
        "examples" to FacadeTestGroup(
            "Examples", listOf(
                FacadeTest("Alignment") { ScreenConstructor(::ExampleAlignmentScreen) },
                FacadeTest("Getting Started > Hello Square") { ScreenConstructor { HelloSquareScreen() } },
                FacadeTest("Getting Started > All the Squares") { ScreenConstructor { AllTheSquaresScreen() } },
                FacadeTest("Getting Started > Squares all the Way Down") { ScreenConstructor { SquaresAllTheWayDownScreen() } },
                FacadeTest("Guessing Game") { ScreenConstructor { GuessingGameScreen() } },
                FacadeTestGroup(
                    "Transform >", listOf(
                        FacadeTest("Visualization Test") { ScreenConstructor(::VisualizationTestScreen) },
                        FacadeTest("Position") { ScreenConstructor(::PositionExampleScreen) },
                    )
                ),
            )
        ),
    )

    // note: the container and data classes are inferred from the screen
    val containerSets: Map<String, TestContainerSet> = mapOf(
        "basic" to TestContainerSet("Basic") {
            container("Single Slot", SingleSlotContainer::class.java) { ContainerScreenFactory(::SingleSlotScreen) }
            container("Slot Occlusion", OcclusionContainer::class.java) { ContainerScreenFactory(::OcclusionScreen) }
            container("Fluid Slot", FluidSlotContainer::class.java) { ContainerScreenFactory(::FluidSlotScreen) }
        },
        "jei_compat" to TestContainerSet("JEI Compat") {
            container("Exclusion Areas", JeiExclusionAreasContainer::class.java) { ContainerScreenFactory(::JeiExclusionAreasScreen) }
            container("Ghost Slots", GhostSlotContainer::class.java) { ContainerScreenFactory(::GhostSlotScreen) }
            container("Ingredient Layers", JeiIngredientLayerContainer::class.java) { ContainerScreenFactory(::JeiIngredientLayerScreen) }
        },
    )

    val simpleContainerType: FacadeContainerType<SimpleContainer>
    val simpleInventoryContainerType: FacadeContainerType<SimpleInventoryContainer>
    val testContainerSelectorContainerType: FacadeContainerType<TestContainerSelectorContainer>
    val backpackContainerType: FacadeContainerType<BackpackContainer>
    val backpackItem: Item

    init {
        groups.forEach { (id, group) ->
            +TestScreenConfig(id, group.name, itemGroup) {
                customScreen {
                    TestListScreen(group.name, group.tests)
                }
            }
        }

        simpleInventoryContainerType = FacadeContainerType(SimpleInventoryContainer::class.java)
        simpleInventoryContainerType.registryName = loc("ll-facade-test", "simple_inventory")
        +TestBlock(TestBlockConfig("simple_inventory", "Simple Inventory") {
            tile(::SimpleInventoryTile)

            rightClick.server {
                simpleInventoryContainerType.open(
                    player as ServerPlayerEntity,
                    LiteralText("Simple Inventory"),
                    pos
                )
            }
        })

        simpleContainerType = FacadeContainerType(SimpleContainer::class.java)
        simpleContainerType.registryName = loc("ll-facade-test", "simple_container")
        +TestItem(TestItemConfig("simple_container", "Simple Container") {
            rightClick.server {
                simpleContainerType.open(player as ServerPlayerEntity, StringTextComponent("Simple Container"))
            }
        })

        testContainerSelectorContainerType = FacadeContainerType(TestContainerSelectorContainer::class.java)
        testContainerSelectorContainerType.registryName = loc("ll-facade-test", "container_selector")
        containerSets.forEach { (id, containerSet) ->
            +TestBlock(TestBlockConfig(id + "_container_set", "${containerSet.name} Container Set") {
                tile<TestContainerTile> { TestContainerTile(it, containerSet) }

                rightClick.server {
                    testContainerSelectorContainerType.open(
                        player as ServerPlayerEntity,
                        StringTextComponent(containerSet.name),
                        pos
                    )
                }
            })
        }

        backpackContainerType = FacadeContainerType(BackpackContainer::class.java)
        backpackContainerType.registryName = loc("ll-facade-test", "backpack")
        backpackItem = BackpackItem(itemGroup)
        backpackItem.registryName = loc("ll-facade-test", "backpack")
        +backpackItem

        +UnitTestSuite("rmvalue") {
            add<RMValueTests>()
        }

        +DirtSetterItem(Item.Properties().group(itemGroup).maxStackSize(1)).also {
            it.registryName = loc("ll-facade-test", "dirt_setter")
        }
    }

    @SubscribeEvent
    fun registerContainers(e: RegistryEvent.Register<ContainerType<*>>) {
        e.registry.register(simpleContainerType)
        e.registry.register(simpleInventoryContainerType)
        e.registry.register(testContainerSelectorContainerType)
        e.registry.register(backpackContainerType)
        for ((_, containerSet) in containerSets) {
            for (type in containerSet.types) {
                e.registry.register(type.containerType)
            }
        }
        ExampleModContainers.registerContainers(e)
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        ScreenManager.registerFactory(simpleContainerType, ::SimpleContainerScreen)
        ScreenManager.registerFactory(simpleInventoryContainerType, ::SimpleInventoryContainerScreen)
        ScreenManager.registerFactory(testContainerSelectorContainerType, ::TestContainerSelectorScreen)
        ScreenManager.registerFactory(backpackContainerType, ::BackpackContainerScreen)
        for ((_, containerSet) in containerSets) {
            for (type in containerSet.types) {
                @Suppress("UNCHECKED_CAST")
                ScreenManager.registerFactory(
                    type.containerType as ContainerType<FacadeContainer>
                ) { container, inventory, title ->
                    (type.screenFactory.getClientFunction() as ContainerScreenFactory<FacadeContainer>)
                        .create(container, inventory, title)
                }
            }
        }
        ExampleModContainers.clientSetup(event)
    }
}

internal val logger = LibrarianLibFacadeTestMod.makeLogger(null)
