package com.teamwizardry.librarianlib.client.core

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.common.util.builders.json
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.File
import java.nio.file.Paths


/**
 * Created by WireSegal on 9/27/2016.
 *
 * This works in the default dev environment.
 */
@SideOnly(Side.CLIENT)
object JsonGenerationUtils {

    private val s = File.separator

    private operator fun String.unaryPlus(): String {
        return this.replace("/", s)
    }

    fun getPathsForBlockstate(block: Block, stateMapper: IStateMapper? = null): Array<String> {
        val mapped = (stateMapper ?: DefaultStateMapper()).putStateModelLocations(block)
        val files = mapped.map {
            getPathForMRL(it.value)
        }.toSet().toTypedArray()
        return files
    }

    fun getPathForMRL(modelResourceLocation: ModelResourceLocation): String {
        return getAssetPath(modelResourceLocation.resourceDomain) + + "/blockstates/${modelResourceLocation.resourcePath}.json"
    }

    fun getPathForBlockModel(block: Block): String {
        val registryName = block.registryName
        return getAssetPath(registryName.resourceDomain) + + "/models/block/${registryName.resourcePath}.json"
    }

    fun getPathForItemModel(item: Item, variantName: String? = null): String {
        val registryName = item.registryName
        val varname = if (item is ItemBlock || variantName == null) registryName.resourcePath else variantName
        return getAssetPath(registryName.resourceDomain) + + "/models/item/$varname.json"
    }

    fun getAssetPath(modid: String): String {
        return Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent.toString() + + "/src/main/resources/assets/$modid"
    }

    fun generateBaseItemModel(item: Item, variantName: String? = null): JsonElement {
        val registryName = item.registryName
        val varname = variantName ?: registryName.resourcePath
        if (item is ItemBlock) return json { obj("parent" to "${registryName.resourceDomain}:block/$varname") }
        return json { obj (
            "parent" to "item/generated",
            "textures" to obj (
                "layer0" to "${registryName.resourceDomain}:items/$varname"
            )
        )}
    }

    fun generateBaseBlockModel(block: Block): JsonElement {
        val registryName = block.registryName
        return json { obj (
            "parent" to "block/cube_all",
            "textures" to obj (
                "all" to "${registryName.resourceDomain}:blocks/${registryName.resourcePath}"
            )
        )}
    }

    fun generateBaseBlockStates(block: Block, stateMapper: IStateMapper? = null): Map<String, JsonElement> {
        val registryName = block.registryName
        val mapped = (stateMapper ?: DefaultStateMapper()).putStateModelLocations(block)
        val files = getPathsForBlockstate(block, stateMapper)
        return mapOf(*(files.map { file ->
            val keypairs = mapped.filter { keypair ->
                getPathForMRL(keypair.value) == file
            }.toList()
            file to json { obj (
                "variants" to mapOf(*Array(keypairs.size) {
                    keypairs[it].second.variant to json { obj("model" to registryName.toString()) }
                })
            ) }
        }.toTypedArray()))
    }

}
