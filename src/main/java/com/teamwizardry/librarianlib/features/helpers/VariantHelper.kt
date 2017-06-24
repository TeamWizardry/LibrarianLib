package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import com.teamwizardry.librarianlib.features.base.IVariantHolder
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import java.util.*

/**
 * Tools to implement variants easily.
 */
object VariantHelper {

    /**
     * Transforms a string to snake case. Allows people to be lazy in migration.
     */
    @JvmStatic
    fun toSnakeCase(str: String): String {
        val string = str.replace("-", "_")

        if (string.toUpperCase(Locale.ROOT) == string)
            return string.toLowerCase(Locale.ROOT)
        else if (string.toLowerCase(Locale.ROOT) == string)
            return string

        val split = mutableListOf<String>()
        var lastTokenUpper = true
        var lastIndex = 0
        for ((index, token) in string.withIndex()) if (token.isUpperCase()) {
            if (!lastTokenUpper) {
                split.add(string.substring(lastIndex, index))
                lastIndex = index
            }
            lastTokenUpper = true
        } else lastTokenUpper = token == '_'

        split.add(string.substring(lastIndex))

        return split.joinToString("_") { it.toLowerCase(Locale.ROOT) }
    }

    /**
     * Transforms a path string to snake case. Allows people to be lazy in migration.
     */
    @JvmStatic
    fun pathToSnakeCase(str: String): String {
        val parts = str.replace("\\", "/").split("/")
        return parts.joinToString("/") { toSnakeCase(it) }
    }

    /**
     * All items which use this method in their constructor should implement the setUnlocalizedNameForItem provided below in their setUnlocalizedName.
     */
    @JvmStatic
    fun <T> setupItem(item: T, name: String, variants: Array<out String>, modCreativeTab: (() -> ModCreativeTab?)? = null): Array<out String> where T : Item, T : IModItemProvider {
        var variantTemp = variants.map { toSnakeCase(it) }.toTypedArray()
        item.unlocalizedName = name
        if (variantTemp.size > 1)
            item.hasSubtypes = true

        if (variantTemp.isEmpty())
            variantTemp = arrayOf(toSnakeCase(name))

        ModelHandler.registerVariantHolder(item)

        if (modCreativeTab != null)
            ModCreativeTab.itemsToTab.put(item, modCreativeTab)
        return variantTemp
    }

    /**
     * All blocks which use this method in their constructor should implement the setUnlocalizedNameForBlock provided below in their setUnlocalizedName.
     * After caching variants using this, call [finishSetupBlock].
     */
    @JvmStatic
    fun beginSetupBlock(name: String, variants: Array<out String>): Array<out String> {
        var variantTemp = variants.map { toSnakeCase(it) }.toTypedArray()
        if (variants.isEmpty())
            variantTemp = arrayOf(toSnakeCase(name))
        return variantTemp
    }

    @JvmStatic
    @JvmOverloads
    fun <T> finishSetupBlock(block: T, name: String, itemForm: ItemBlock?, modCreativeTab: (() -> ModCreativeTab?)? = null) where T : Block, T : IModBlockProvider {
        block.unlocalizedName = name
        if (itemForm == null)
            ModelHandler.registerVariantHolder(block)
        if (modCreativeTab != null)
            ModCreativeTab.blocksToTab.put(block, modCreativeTab)
    }

    @JvmStatic
    fun setUnlocalizedNameForItem(item: Item, modId: String, name: String) {
        val rl = ResourceLocation(modId, toSnakeCase(name))
        RegistrationHandler.register(item, rl)
    }

    @JvmStatic
    fun setUnlocalizedNameForBlock(block: Block, modId: String, name: String, itemForm: ItemBlock?) {
        val snakeName = toSnakeCase(name)
        block.setRegistryName(snakeName)
        RegistrationHandler.register(block)
        if (itemForm != null)
            RegistrationHandler.register(itemForm, ResourceLocation(modId, snakeName))
    }

}
