package com.teamwizardry.librarianlib.testcore.resources

import com.google.gson.Gson
import com.teamwizardry.librarianlib.testcore.content.TestBlockConfig
import net.minecraft.util.Identifier

internal object TestBlockModelGenerator {
    fun blockStateLoc(blockConfig: TestBlockConfig) = Identifier.of(blockConfig.id.namespace, "blockstates/${blockConfig.id.path}.json")
    fun itemModelLoc(blockConfig: TestBlockConfig) = Identifier.of(blockConfig.id.namespace, "models/item/${blockConfig.id.path}.json")

    fun generateBlockStates(blockConfig: TestBlockConfig): Pair<Identifier, String> {
        val model = getBaseModel(blockConfig)

        val variants = if (blockConfig.directional) {
            mapOf(
                "facing=up" to mapOf("model" to model),
                "facing=down" to mapOf("model" to model, "x" to 180),
                "facing=north" to mapOf("model" to model, "x" to 90),
                "facing=south" to mapOf("model" to model, "x" to 90, "y" to 180),
                "facing=west" to mapOf("model" to model, "x" to 90, "y" to 270),
                "facing=east" to mapOf("model" to model, "x" to 90, "y" to 90),
            )
        } else {
            mapOf(
                "" to mapOf("model" to model),
            )
        }
        val blockstates = mapOf("variants" to variants)

        return blockStateLoc(blockConfig) to Gson().toJson(blockstates)
    }

    fun generateItemModel(blockConfig: TestBlockConfig): Pair<Identifier, String> {
        val model = getBaseModel(blockConfig)
        val itemModel = mapOf(
            "parent" to model
        )
        return itemModelLoc(blockConfig) to Gson().toJson(itemModel)
    }

    private fun getBaseModel(blockConfig: TestBlockConfig): String {
        val modelName = "${if (blockConfig.directional) "directional" else "normal"}/${if (blockConfig.transparent) "transparent" else "solid"}"
        return "liblib_testcore:block/test_block/${modelName}"
    }
}