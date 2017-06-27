package com.teamwizardry.librarianlib.features.utilities

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.features.helpers.VariantHelper.toSnakeCase
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.registries.IForgeRegistryEntry
import java.io.File
import java.nio.file.Paths


/**
 * Created by WireSegal on 9/27/2016.
 *
 * This works in the default dev environment.
 */
object JsonGenerationUtils {

    /**
     * Sets your mod's default path prefix. By default, paths are `src/main/resources/assets`.
     * Trailing and leading slashes will be removed. Do not use backslashes.
     */
    @JvmStatic
    fun setPathPrefix(prefix: String) {
        paths[currentModId] = prefix.trim('/')
    }

    fun getPathPrefix(modid: String): String {
        return paths[modid] ?: "src/main/resources/assets"
    }

    private val paths = mutableMapOf<String, String>()

    private val s = File.separator

    private operator fun String.unaryPlus(): String {
        return this.replace("/", JsonGenerationUtils.s)
    }

    fun getPathForBaseBlockstate(block: IForgeRegistryEntry<*>): String {
        val registryName = block.registryName
        return getAssetPath(registryName!!.resourceDomain) + +"/blockstates/${registryName!!.resourcePath}.json"
    }

    fun getPathsForBlockstate(block: Block, stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)? = null): Array<String> {
        val mapped = (stateMapper ?: { DefaultStateMapper().putStateModelLocations(it) })(block)
        val files = mapped.map {
            getPathForMRL(it.value)
        }.toSet().toTypedArray()
        return files
    }

    fun getPathForMRL(modelResourceLocation: ModelResourceLocation): String {
        return getAssetPath(modelResourceLocation.resourceDomain) + +"/blockstates/${modelResourceLocation.resourcePath}.json"
    }

    fun getPathForBlockModel(block: Block): String {
        return getPathForBlockModel(block, block.registryName!!.resourcePath)
    }

    fun getPathForBlockModel(block: Block, variant: String): String {
        return getAssetPath(block.registryName!!.resourceDomain) + +"/models/block/$variant.json"
    }

    fun getPathForItemModel(item: Item, variantName: String? = null): String {
        val registryName = item.registryName
        val varname = variantName ?: registryName!!.resourcePath
        return getAssetPath(registryName!!.resourceDomain) + +"/models/item/$varname.json"
    }

    fun getAssetPath(modid: String): String {
        return Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent.toString() + +"/${getPathPrefix(modid)}/$modid"
    }

    fun getPathForRecipe(modid: String, name: String): String {
        return +"${getAssetPath(modid)}/recipes/${toSnakeCase(name)}.json"
    }

    fun generateBaseItemModel(item: Item, variantName: String? = null, parent: String = "item/generated"): JsonElement {
        val registryName = item.registryName
        val varname = variantName ?: registryName!!.resourcePath
        if (item is ItemBlock) return json { obj("parent" to "${registryName!!.resourceDomain}:block/$varname") }
        return generateRegularItemModel(item, variantName, parent)
    }

    fun generateRegularItemModel(item: Item, variantName: String? = null, parent: String = "item/generated"): JsonElement {
        val registryName = item.registryName
        val varname = variantName ?: registryName!!.resourcePath
        return json {
            obj(
                    "parent" to parent,
                    "textures" to obj(
                            "layer0" to "${registryName!!.resourceDomain}:items/$varname"
                    )
            )
        }
    }

    fun generateBaseBlockModel(block: Block): JsonElement {
        return generateBaseBlockModel(block, block.registryName!!.resourcePath)
    }

    fun generateBaseBlockModel(block: Block, variant: String): JsonElement {
        val registryName = block.registryName
        return json {
            obj(
                    "parent" to "block/cube_all",
                    "textures" to obj(
                            "all" to "${registryName!!.resourceDomain}:blocks/$variant"
                    )
            )
        }
    }

    fun generateBaseBlockStates(block: Block, stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)? = null): Map<String, JsonElement> {
        val registryName = block.registryName
        return generateBlockStates(block, stateMapper) {
            json { obj("model" to registryName.toString()) }
        }
    }

    inline fun generateBlockStates(block: Block, noinline stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?, makeVariant: (variant: String) -> JsonElement): Map<String, JsonElement> {
        val mapped = (stateMapper ?: { DefaultStateMapper().putStateModelLocations(it) })(block)
        val files = getPathsForBlockstate(block, stateMapper)
        return files.associate { file ->
            val keypairs = mapped.filter { keypair ->
                getPathForMRL(keypair.value) == file
            }.toList()
            file to json {
                obj(
                        "variants" to keypairs.associate {
                            val variant = it.second.variant
                            variant to makeVariant(variant)
                        })
            }
        }
    }


    var generatedFiles = mutableListOf<String>()


}
