package com.teamwizardry.librarianlib.client.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.base.*
import com.teamwizardry.librarianlib.common.base.block.IBlockColorProvider
import com.teamwizardry.librarianlib.common.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.IStringSerializable
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * @author WireSegal
 * Created at 2:12 PM on 3/20/16.
 */
object ModelHandler {

    // Easy access
    private val debug = LibrarianLib.DEV_ENVIRONMENT
    private var modName = ""
    private val namePad: String
        get() = Array(modName.length) { " " }.joinToString("")

    private val variantCache = HashMap<String, MutableList<IVariantHolder>>()

    /**
     * This is Mod name -> (Variant name -> MRL), specifically for ItemMeshDefinitions.
     */
    @JvmField
    @SideOnly(Side.CLIENT)
    val resourceLocations = HashMap<String, HashMap<String, ModelResourceLocation>>()

    /**
     * Use this method to inject your item into the list to be loaded at the end of preinit and colorized at the end of init.
     */
    @JvmStatic
    fun registerVariantHolder(holder: IVariantHolder) {
        val name = Loader.instance().activeModContainer()?.modId ?: return
        variantCache.getOrPut(name) { mutableListOf() }.add(holder)
    }

    @SideOnly(Side.CLIENT)
    private fun addToCachedLocations(name: String, mrl: ModelResourceLocation) {
        resourceLocations.getOrPut(modName) { hashMapOf() }.put(name, mrl)
    }

    @SideOnly(Side.CLIENT)
    fun preInit() {
        for ((modid, holders) in variantCache) {
            modName = modid
            log("$modName | Registering models")
            for (holder in holders.sortedBy { (255 - it.variants.size).toChar() + if (it is IModBlockProvider) "b" else "I" + if (it is IModItemProvider) it.providedItem.registryName.resourcePath else "" }) {
                registerModels(holder)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun init() {
        val itemColors = Minecraft.getMinecraft().itemColors
        val blockColors = Minecraft.getMinecraft().blockColors
        for ((modid, holders) in variantCache) {
            modName = modid
            log("$modName | Registering colors")
            for (holder in holders) {

                if (holder is IItemColorProvider && holder is IModItemProvider) {
                    val color = holder.getItemColor()
                    if (color != null) {
                        log("$namePad | Registering item color for ${holder.providedItem.registryName.resourcePath}")
                        itemColors.registerItemColorHandler(color, holder.providedItem)
                    }
                }

                if (holder is IModBlockProvider && holder is IBlockColorProvider) {
                    val color = holder.getBlockColor()
                    if (color != null) {
                        log("$namePad | Registering block color for ${holder.providedBlock.registryName.resourcePath}")
                        blockColors.registerBlockColorHandler(color, holder.providedBlock)
                    }
                }

            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerModels(holder: IVariantHolder) {
        if (holder is IModItemProvider && holder.getCustomMeshDefinition() != null)
            ModelLoader.setCustomMeshDefinition(holder.providedItem, holder.getCustomMeshDefinition())
        else
            registerModels(holder, holder.variants, false)

        if (holder is IExtraVariantHolder)
            registerModels(holder, holder.extraVariants, true)
    }

    @SideOnly(Side.CLIENT)
    fun registerModels(holder: IVariantHolder, variants: Array<out String>, extra: Boolean) {
        if (holder is IModBlockProvider && !extra) {
            val variantEnum = holder.variantEnum

            val mapper = holder.getStateMapper()
            if (mapper != null)
                ModelLoader.setCustomStateMapper(holder.providedBlock, mapper)

            if (variantEnum != null && holder is IModItemProvider) {
                registerVariantsDefaulted(holder.providedItem, holder.providedBlock, variantEnum, "variant")
                return
            }
        }

        if (holder is IModItemProvider) {
            val item = holder.providedItem
            for (variant in variants.withIndex()) {
                if (variant.index == 0) {
                    var print = "${namePad} | Registering "

                    if (variant.value != item.registryName.resourcePath || variants.size != 1 || extra)
                        print += "${if (extra) "extra " else ""}variant${if (variants.size == 1) "" else "s"} of "

                    print += if (item is IModBlockProvider) "block" else "item"
                    print += " ${item.registryName.resourcePath}"
                    log(print)
                }

                if ((variant.value != item.registryName.resourcePath || variants.size != 1))
                    log("$namePad |  Variant #${variant.index + 1}: ${variant.value}")

                val model = ModelResourceLocation(ResourceLocation(modName, variant.value).toString(), "inventory")
                if (!extra) {
                    ModelLoader.setCustomModelResourceLocation(item, variant.index, model)
                    addToCachedLocations(getKey(item, variant.index), model)
                } else {
                    ModelBakery.registerItemVariants(item, model)
                    addToCachedLocations(variant.value, model)
                }
            }
        }

    }

    @SideOnly(Side.CLIENT)
    private fun registerVariantsDefaulted(item: Item, block: Block, enumclazz: Class<*>, variantHeader: String) {
        val locName = block.registryName.toString()
        if (enumclazz.enumConstants != null)
            for (e in enumclazz.enumConstants) {
                if (e is IStringSerializable && e is Enum<*>) {
                    val variantName = variantHeader + "=" + e.name

                    if (e.ordinal == 0) {
                        var print = "${namePad} | Registering "
                        if (variantName != item.registryName.resourcePath || enumclazz.enumConstants.size != 1)
                            print += "variant" + (if (enumclazz.enumConstants.size == 1) "" else "s") + " of "
                        print += if (item is ItemBlock) "block" else "item"
                        print += " " + item.registryName.resourcePath
                        log(print)
                    }
                    if (e.name != item.registryName.resourcePath || enumclazz.enumConstants.size != 1)
                        log("$namePad |  Variant #${e.ordinal + 1}: $variantName")

                    val loc = ModelResourceLocation(locName, variantName)
                    val i = e.ordinal
                    ModelLoader.setCustomModelResourceLocation(item, i, loc)
                    addToCachedLocations(getKey(item, i), loc)
                }
            }

    }

    private fun getKey(item: Item, meta: Int): String {
        return "i_" + item.registryName + "@" + meta
    }

    fun log(text: String) {
        if (debug) LibrarianLog.info(text)
    }
}
