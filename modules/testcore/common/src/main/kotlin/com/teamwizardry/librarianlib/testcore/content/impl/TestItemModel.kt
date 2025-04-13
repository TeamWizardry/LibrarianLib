package com.teamwizardry.librarianlib.testcore.content.impl

//import net.minecraft.util.Identifier
//import pers.solid.brrp.v1.model.ModelJsonBuilder
//import kotlin.random.Random
//
//internal class TestItemModel(val id: Identifier) {
////    val colorProvider: ItemColorProvider
//    val model: ModelJsonBuilder
//
//    init {
//        val rng = Random(id.hashCode())
//
//        val color1 = colors.random(rng)
//        val color2 = colors.random(rng)
//
////        see:
////          assets/minecraft/models/item/template_spawn_egg.json
////          assets/minecraft/items/allay_spawn_egg.json
////        colorProvider = ItemColorProvider { _, tintIndex ->
////            when(tintIndex) {
////                2 -> color1
////                3 -> color2
////                else -> 0xffffff
////            }
////        }
//
//        val head = heads.random(rng)
//        val handle = handles.random(rng)
//        val tail = tails.random(rng)
//
//        val prefix = "liblib-testcore:item/test_tool/"
//        model = ModelJsonBuilder.create("item/generated")
//            .addTexture("layer0", prefix + head.texture)
//            .addTexture("layer1", prefix + handle.background)
//            .addTexture("layer2", prefix + handle.color1)
//            .addTexture("layer3", prefix + handle.color2)
//            .addTexture("layer4", prefix + tail.texture)
//    }
//
//    companion object {
//        val handles = listOf(
//            Handle("handle/plain"),
//            Handle("handle/stone"),
////            Handle("handle/pearl", "handle/pearl_color"),
////            Handle("handle/one_band", "handle/one_band_color"),
////            Handle("handle/two_bands_plain", "handle/two_bands_color1", "handle/two_bands_color2"),
////            Handle("handle/two_bands_stone", "handle/two_bands_color1", "handle/two_bands_color2"),
//            Handle("handle/s"),
//            Handle("handle/guard"),
//            Handle("handle/strap"),
//        )
//
//        val heads = listOf(
//            Part("head/plain"),
//            Part("head/tip"),
//            Part("head/barb"),
//            Part("head/pommel"),
//        )
//
//        val tails = listOf(
//            Part("tail/plain"),
//            Part("tail/tip"),
//            Part("tail/barb"),
//            Part("tail/pommel"),
//        )
//
//        val colors = listOf(
//            0xffffff, // white
//            0x7f7f7f, // gray
//            0xfbfb3d, // yellow
//            0x6bfb3d, // green
//            0x1531ff, // blue
//        )
//    }
//
//    data class Part(val texture: String)
//
//    data class Handle(val background: String, val color1: String = "handle/empty", val color2: String = "handle/empty")
//}
