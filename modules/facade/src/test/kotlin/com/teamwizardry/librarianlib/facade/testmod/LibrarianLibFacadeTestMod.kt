@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.ExtraDataContainer
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.SimpleContainerScreen
import com.teamwizardry.librarianlib.facade.testmod.screens.*
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestScreen
import com.teamwizardry.librarianlib.facade.testmod.value.RMValueTests
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-facade-test")
object LibrarianLibFacadeTestMod: TestMod(LibrarianLibFacadeModule) {
    val groups: List<FacadeTestGroup> = listOf(
        FacadeTestGroup("basics", "Basics", listOf(
            FacadeTest("Empty", ::EmptyTestScreen),
            FacadeTest("zIndex", ::ZIndexTestScreen),
            FacadeTest("Layer Transform", ::LayerTransformTestScreen),
            FacadeTest("Layer MouseOver/MouseOff", ::LayerMouseOverOffTestScreen)
        )),
        FacadeTestGroup("layers", "Layers", listOf(
            FacadeTest("Simple Sprite", ::SimpleSpriteTestScreen),
            FacadeTest("Simple Text", ::SimpleTextTestScreen),
            FacadeTest("DragLayer", ::DragLayerTestScreen)
        )),
        FacadeTestGroup("animations", "Animations/Time", listOf(
            FacadeTest("Animations", ::AnimationTestScreen),
            FacadeTest("Scheduled Callbacks", ::ScheduledCallbacksTestScreen),
            FacadeTest("Scheduled Repeated Callbacks", ::ScheduledRepeatedCallbacksTestScreen)
        )),
        FacadeTestGroup("clipping_compositing", "Clipping/Compositing", listOf(
            FacadeTest("Clip to Bounds", ::ClipToBoundsTestScreen),
            FacadeTest("Masking", ::MaskingTestScreen),
            FacadeTest("Opacity", ::OpacityTestScreen),
            FacadeTest("Blending", ::BlendingTestScreen),
            FacadeTest("Render to FBO Scale", ::RenderFBOScaleTest),
            FacadeTest("Render to Quad Scale", ::RenderQuadScaleTest)
        )),
        FacadeTestGroup("advanced", "Advanced", listOf(
            FacadeTest("Yoga Simple Flex", ::SimpleYogaScreen),
            FacadeTest("Yoga List", ::YogaListScreen),
            FacadeTest("Pastry", ::PastryTestScreen)
        ))
    )

    val simpleContainerType: ContainerType<SimpleContainer>

    init {
        groups.forEach { group ->
            +TestScreenConfig(group.id, group.name, itemGroup) {
                customScreen {
                    TestListScreen(group.name, group.tests)
                }
            }
        }

        simpleContainerType = IForgeContainerType.create { windowId, playerInventory, packetBuffer ->
            SimpleContainer(windowId, playerInventory)
        }
        simpleContainerType.registryName = loc("librarianlib-facade-test", "simple_container")

        +TestItem(TestItemConfig("simple_container", "Simple Container") {
            val provider = object: INamedContainerProvider {
                override fun createMenu(p_createMenu_1_: Int, p_createMenu_2_: PlayerInventory, p_createMenu_3_: PlayerEntity): Container? {
                    return SimpleContainer(p_createMenu_1_, p_createMenu_2_)
                }

                override fun getDisplayName(): ITextComponent {
                    return StringTextComponent("Simple Container")
                }
            }
            rightClick.server {
                ExtraDataContainer.open(player as ServerPlayerEntity, provider)
            }
        })

        +UnitTestSuite("rmvalue") {
            add<RMValueTests>()
        }
    }

    @SubscribeEvent
    fun registerContainers(e: RegistryEvent.Register<ContainerType<*>>) {
        e.registry.register(simpleContainerType)
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        ScreenManager.registerFactory(simpleContainerType, ::SimpleContainerScreen)
    }
}

internal val logger = LibrarianLibFacadeTestMod.makeLogger(null)
