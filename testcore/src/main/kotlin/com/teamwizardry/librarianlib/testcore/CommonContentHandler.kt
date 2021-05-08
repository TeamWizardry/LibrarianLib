package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.objects.TestBlock
import com.teamwizardry.librarianlib.testcore.objects.TestConfig
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import net.minecraft.util.registry.Registry

public class CommonContentHandler private constructor(private val manager: TestModContentManager) {
    public companion object {
        @JvmStatic
        public fun register(manager: TestModContentManager) {
            CommonContentHandler(manager).register()
        }
    }

    private val logger = TestCore.logManager.makeLogger("CommonContentHandler{${manager.modid}}")

    private fun register() {
        logger.debug("Registering ${manager.objects.size} objects for mod ID ${manager.modid}")
        for(config in manager.objects.values) {
            register(config)
        }
    }

    private fun register(config: TestConfig) {
        logger.debug("Registering object ${config.id} with type ${config.javaClass.simpleName}")
        when(config) {
            is TestBlock -> registerBlock(config)
            is TestItem -> registerItem(config)
        }
    }

    private fun registerBlock(config: TestBlock) {
        Registry.register(Registry.BLOCK, config.id, config.instance)
        Registry.register(Registry.ITEM, config.id, config.itemInstance)
    }

    private fun registerItem(config: TestItem) {
        Registry.register(Registry.ITEM, config.id, config.instance)
    }
}