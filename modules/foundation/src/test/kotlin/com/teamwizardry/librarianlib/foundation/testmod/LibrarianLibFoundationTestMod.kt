package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import com.teamwizardry.librarianlib.foundation.example.ExampleModContainers
import com.teamwizardry.librarianlib.foundation.example.ExampleModItems
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestItem
import net.minecraftforge.fml.common.Mod

@Mod("ll-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
    init {
        setLoggerBaseName("LibrarianLib: Foundation Test")

        ModTiles.registerTileEntities(registrationManager)
        ModBlocks.registerBlocks(registrationManager)
        ModItems.registerItems(registrationManager)
        ModEntities.registerEntities(registrationManager)
        ModTags.registerTagsDatagen(registrationManager)
        ModCapabilities.registerCapabilities(registrationManager)
        ModContainers.registerContainers(registrationManager)
        ModSounds.registerSounds(registrationManager)

        ExampleModItems.register(registrationManager)
        ExampleModContainers.register(registrationManager)

        registrationManager.datagen.add(ModRecipes)
        registrationManager.itemGroupIcon = ModItems.testItem

        logger.info("Default logger, should be called `LibrarianLib: Foundation Test`")
        val nullLogger = makeLogger(null)
        nullLogger.info("Null logger, should be called `LibrarianLib: Foundation Test`")
        val testNameLogger = makeLogger("Test Name")
        testNameLogger.info("Logger name should be `LibrarianLib: Foundation Test (Test Name)`")
        val testItemLogger = makeLogger<TestItem>() // in java, makeLogger(TestItem.class)
        testItemLogger.info("Logger name should be `LibrarianLib: Foundation Test (TestItem)`")
    }
}
