package com.teamwizardry.librarianlib.features.utilities

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.base.ICustomTexturePath
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

operator fun String.unaryPlus() = this.replace('/', File.separatorChar)

fun getPathForBaseBlockstate(block: FileDsl<Block>): String = getPathForBaseBlockstate(block.value)

fun getPathForBaseBlockstate(block: IForgeRegistryEntry<*>): String =
        getAssetPath(block.key.namespace) + +"/blockstates/${block.key.path}.json"

fun getPathsForBlockstate(block: FileDsl<Block>, stateMapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)? = null): Array<String> {
    val mapped = (stateMapper ?: { DefaultStateMapper().putStateModelLocations(it) })(block.value)
    return mapped.map {
        getPathForMRL(it.value)
    }.toSet().toTypedArray()
}

fun getPathForMRL(modelResourceLocation: ModelResourceLocation) =
        getAssetPath(modelResourceLocation.namespace) + +"/blockstates/${modelResourceLocation.path}.json"

fun getPathForModel(type: String, namespace: String, name: String) =
        getAssetPath(namespace) + +"/models/$type/$name.json"

fun getPathForBlockModel(block: FileDsl<Block>) = getPathForBlockModel(block, block.key.path)

fun getPathForBlockModel(block: FileDsl<Block>, variant: String) =
        getPathForModel("block", block.key.namespace, variant)

fun getPathForItemModel(item: FileDsl<Item>, variantName: String? = null): String {
    val varname = variantName ?: item.key.path
    return getPathForModel("item", varname.substringBefore(':', item.key.namespace), varname.substringAfter(':'))
}

fun getAssetPath(modid: String) =
        Paths.get(Minecraft.getMinecraft().gameDir.absolutePath).parent.parent.toString() + +"/${getPathPrefix(modid)}/$modid"

fun getPathForRecipe(modid: String, name: String) =
        +"${getAssetPath(modid)}/recipes/${toSnakeCase(name)}.json"

fun getPathForSounds(modid: String) = +"${getAssetPath(modid)}/sounds.json"

fun generateBaseItemModel(item: FileDsl<Item>, variantName: String? = null, parent: String = "item/generated"): JsonObject {
    val varname = variantName ?: item.key.path
    if (item is ItemBlock) return jsonObject { "parent"("${item.key.namespace}:block/$varname") }
    return generateRegularItemModel(item, variantName, parent)
}

fun generateRegularItemModel(item: FileDsl<Item>, variantName: String? = null, parent: String = "item/generated"): JsonObject {
    val varname = variantName ?: item.key.path
    val path = (item.value as? ICustomTexturePath)?.texturePath(varname) ?: "items/${varname.substringAfter(':')}"
    return jsonObject {
        "parent"(parent)
        "textures" {
            "layer0"("${varname.substringBefore(':', item.key.namespace)}:$path")
        }
    }
}

fun generateBaseBlockModel(block: FileDsl<Block>): JsonObject {
    return generateBaseBlockModel(block, block.key.path)
}

fun generateBaseBlockModel(block: FileDsl<Block>, variant: String): JsonObject {
    val path = (block.value as? ICustomTexturePath)?.texturePath(variant) ?: "blocks/$variant"
    return jsonObject {
        "parent"("block/cube_all")
        "textures" {
            "all"("${block.key.namespace}:$path")
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


