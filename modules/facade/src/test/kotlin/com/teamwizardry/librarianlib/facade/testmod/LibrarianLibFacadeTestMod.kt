@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.testmod.screens.*
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestScreen
import com.teamwizardry.librarianlib.facade.testmod.value.RMValueTests
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import net.minecraftforge.fml.common.Mod
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

    init {
        groups.forEach { group ->
            +TestScreenConfig(group.id, group.name, itemGroup) {
                customScreen {
                    TestListScreen(group.name, group.tests)
                }
            }
        }

        +UnitTestSuite("rmvalue") {
            add<RMValueTests>()
        }
    }
}



internal val logger = LibrarianLibFacadeTestMod.makeLogger(null)
