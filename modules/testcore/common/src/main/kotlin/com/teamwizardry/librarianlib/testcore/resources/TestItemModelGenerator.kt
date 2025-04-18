package com.teamwizardry.librarianlib.testcore.resources

import com.google.gson.Gson
import net.minecraft.util.Identifier
import kotlin.random.Random

internal object TestItemModelGenerator {
    fun modelLoc(id: Identifier) = Identifier.of(id.namespace, "models/item/${id.path}.json")
    fun modelDefLoc(id: Identifier) = Identifier.of(id.namespace, "items/${id.path}.json")

    fun generateModel(id: Identifier): Pair<Identifier, String> {
        val rng = Random(id.hashCode())

        val head = heads.random(rng)
        val handle = handles.random(rng)
        val tail = tails.random(rng)
        val prefix = "liblib-testcore:item/test_tool/"
        val modelJson = mapOf(
            "parent" to "item/generated",
            "textures" to mapOf(
                "layer0" to prefix + head.texture,
                "layer1" to prefix + handle.background,
                "layer2" to prefix + handle.color1,
                "layer3" to prefix + handle.color2,
                "layer4" to prefix + tail.texture,
            )
        )
        return modelLoc(id) to Gson().toJson(modelJson)
    }

    fun generateModelDef(id: Identifier): Pair<Identifier, String> {
        val json = mapOf(
            "model" to mapOf(
                "type" to "minecraft:model",
                "model" to "${id.namespace}:item/${id.path}"
            )
        )
        return modelDefLoc(id) to Gson().toJson(json)
    }

    val handles = listOf(
        Handle("handle/plain"),
        Handle("handle/stone"),
//        Handle("handle/pearl", "handle/pearl_color"),
//        Handle("handle/one_band", "handle/one_band_color"),
//        Handle("handle/two_bands_plain", "handle/two_bands_color1", "handle/two_bands_color2"),
//        Handle("handle/two_bands_stone", "handle/two_bands_color1", "handle/two_bands_color2"),
        Handle("handle/s"),
        Handle("handle/guard"),
        Handle("handle/strap"),
    )

    val heads = listOf(
        Part("head/plain"),
        Part("head/tip"),
        Part("head/barb"),
        Part("head/pommel"),
    )

    val tails = listOf(
        Part("tail/plain"),
        Part("tail/tip"),
        Part("tail/barb"),
        Part("tail/pommel"),
    )

    val colors = listOf(
        0xffffff, // white
        0x7f7f7f, // gray
        0xfbfb3d, // yellow
        0x6bfb3d, // green
        0x1531ff, // blue
    )

    data class Part(val texture: String)

    data class Handle(val background: String, val color1: String = "handle/empty", val color2: String = "handle/empty")
}
