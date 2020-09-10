package com.teamwizardry.librarianlib.core.testmod

import com.teamwizardry.librarianlib.core.LibrarianLibCoreModule
import com.teamwizardry.librarianlib.core.testmod.tests.EasingTests
import com.teamwizardry.librarianlib.testbase.TestMod
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.block.Block
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager

@Suppress("UNUSED_PARAMETER")
@Mod("librarianlib-test")
object LibrarianLibCoreTestMod: TestMod(LibrarianLibCoreModule) {
    init {
        +UnitTestSuite("easings") {
            add<EasingTests>()
        }
    }
}

internal val logger = LibrarianLibCoreTestMod.makeLogger(null)
