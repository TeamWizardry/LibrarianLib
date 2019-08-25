package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import com.teamwizardry.librarianlib.virtualresources.VirtualResources
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-testbase")
class LibTestBaseModule : LibrarianLibModule("testbase", logger) {

    override fun clientSetup(event: FMLClientSetupEvent) {
        VirtualResources.client.add(ResourceLocation(modid, "lang/en_us.json")) {
            val keys = languageKeys()
            return@add "{\n" + keys.map {
                "    '${it.key}': \"${it.value.replace("\n", "\\n").replace("\"", "\\\"")}\""
            }.joinToString(",\n") + "\n}"
        }
    }

    companion object {
        private val mods = mutableListOf<TestMod>()

        private fun languageKeys(): Map<String, String> {
            val keys = mutableMapOf<String, String>()
            mods.forEach { mod ->
                mod.items.forEach forEachItem@{ item ->
                    if(item !is TestItem) return@forEachItem
                    keys[Util.makeTranslationKey("item", item.registryName)] = item.config.name
                }
            }
            return keys
        }

        internal fun add(mod: TestMod) {
            mods.add(mod)
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Test Base")
