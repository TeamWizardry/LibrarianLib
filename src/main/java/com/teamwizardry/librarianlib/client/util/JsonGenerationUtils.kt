package com.teamwizardry.librarianlib.client.util

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.client.core.LibLibClientMethodHandles
import com.teamwizardry.librarianlib.common.core.json
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.nio.file.Paths


/**
 * Created by Elad on 9/27/2016.
 *
 * This works in the default dev environment.
 */
@SideOnly(Side.CLIENT)
object JsonGenerationUtils {
    fun getPathForBlockstate(block: Block): String {
        val registryName = block.registryName
        return "${getAssetPath(registryName.resourceDomain)}/blockstates/${registryName.resourcePath}.json"
    }

    fun getPathForBlockModel(block: Block): String {
        val registryName = block.registryName
        return "${getAssetPath(registryName.resourceDomain)}/models/block/${registryName.resourcePath}.json"
    }

    fun getPathForItemModel(item: Item): String {
        val registryName = item.registryName
        return "${getAssetPath(registryName.resourceDomain)}/models/item/${registryName.resourcePath}.json"
    }

    fun getAssetPath(modid: String): String {
        return "${Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent}/src/main/resources/assets/$modid/"
    }

    fun generateBaseItemModel(item: Item, variantName: String? = null): JsonObject {
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

    fun generateBaseBlockModel(block: Block): JsonObject {
        val registryName = block.registryName
        return json { obj (
            "parent" to "block/cube_all",
            "textures" to obj (
                "all" to "${registryName.resourceDomain}:items/${registryName.resourcePath}"
            )
        )}
    }

    fun generateBaseBlockState(block: Block): JsonObject {
        val registryName = block.registryName
        val mapper = LibLibClientMethodHandles.getModelManager().blockModelShapes.blockStateMapper
        val mapped = mapper.getVariants(block)
        val vars = json { obj() }
        for ((state, loc) in mapped) {
            vars.add(loc.variant, json { obj("model" to registryName.toString()) })
        }
        return json {
            obj("variants" to vars)
        }
    }

}
