package com.teamwizardry.librarianlib.features.utilities

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.helpers.VariantHelper.toSnakeCase
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.JsonDsl
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
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
 * Sets your mod's default path prefix. By default, paths are `src/main/resources/assets`.
 * Trailing and leading slashes will be removed. Do not use backslashes.
 */
fun setPathPrefix(prefix: String) {
    paths[currentModId] = prefix.trim('/')
}

fun getPathPrefix(modid: String) = paths[modid] ?: "src/main/resources/assets"

private val paths = mutableMapOf<String, String>()

private operator fun String.unaryPlus() = this.replace("/", File.separator)

fun getPathForBaseBlockstate(block: FileDsl<Block>): String = getPathForBaseBlockstate(block.value)

fun getPathForBaseBlockstate(block: IForgeRegistryEntry<*>): String =
        getAssetPath(block.key.resourceDomain) + +"/blockstates/${block.key.resourcePath}.json"

fun getPathsForBlockstate(block: FileDsl<Block>, stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)? = null): Array<String> {
    val mapped = (stateMapper ?: { DefaultStateMapper().putStateModelLocations(it) })(block.value)
    return mapped.map {
        getPathForMRL(it.value)
    }.toSet().toTypedArray()
}

fun getPathForMRL(modelResourceLocation: ModelResourceLocation) =
        getAssetPath(modelResourceLocation.resourceDomain) + +"/blockstates/${modelResourceLocation.resourcePath}.json"

fun getPathForBlockModel(block: FileDsl<Block>) = getPathForBlockModel(block, block.key.resourcePath)

fun getPathForBlockModel(block: FileDsl<Block>, variant: String) =
        getAssetPath(block.key.resourceDomain) + +"/models/block/$variant.json"

fun getPathForItemModel(item: FileDsl<Item>, variantName: String? = null): String {
    val varname = variantName ?: item.key.resourcePath
    return getAssetPath(item.key.resourceDomain) + +"/models/item/$varname.json"
}

fun getAssetPath(modid: String) =
        Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent.toString() + +"/${getPathPrefix(modid)}/$modid"

fun getPathForRecipe(modid: String, name: String) =
        +"${getAssetPath(modid)}/recipes/${toSnakeCase(name)}.json"

fun getPathForSounds(modid: String) = +"${getAssetPath(modid)}/sounds.json"

fun generateBaseItemModel(item: FileDsl<Item>, variantName: String? = null, parent: String = "item/generated"): JsonObject {
    val varname = variantName ?: item.key.resourcePath
    if (item is ItemBlock) return jsonObject { "parent"("${item.key.resourceDomain}:block/$varname") }
    return generateRegularItemModel(item, variantName, parent)
}

fun generateRegularItemModel(item: FileDsl<Item>, variantName: String? = null, parent: String = "item/generated"): JsonObject {
    val varname = variantName ?: item.key.resourcePath
    return jsonObject {
        "parent"(parent)
        "textures" {
            "layer0"("${item.key.resourceDomain}:items/$varname")
        }
    }
}

fun generateBaseBlockModel(block: FileDsl<Block>): JsonObject {
    return generateBaseBlockModel(block, block.key.resourcePath)
}

fun generateBaseBlockModel(block: FileDsl<Block>, variant: String): JsonObject {
    return jsonObject {
        "parent"("block/cube_all")
        "textures" {
            "all"("${block.key.resourceDomain}:blocks/$variant")
        }
    }
}

fun generateBaseBlockStates(dsl: FileDsl<Block>, stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)? = null) {
    generateBlockStates(dsl, stateMapper) {
        jsonObject { "model"(dsl.key) }
    }
}

inline fun generateBlockStates(dsl: FileDsl<Block>, noinline stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?, makeVariant: JsonDsl.(variant: String) -> Unit) {
    val mapped = (stateMapper ?: { DefaultStateMapper().putStateModelLocations(it) })(dsl.value)
    val files = getPathsForBlockstate(dsl, stateMapper)

    for (file in files)
        dsl.apply {
            file to {
                "variants" {
                    for (location in mapped.values) {
                        if (getPathForMRL(location) == file)
                            location.variant {
                                makeVariant(location.variant)
                            }
                    }
                }
            }
        }
}


var generatedFiles = mutableListOf<String>()


