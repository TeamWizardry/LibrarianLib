package com.teamwizardry.librarianlib.features.multilayermodel.data

import com.google.gson.*
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.JsonUtils
import java.lang.reflect.Type

/**
 * Created by TheCodeWarrior
 */
class LibLibBlockPartFace(cullFace: EnumFacing?, tintIndex: Int, texture: String, blockFaceUV: BlockFaceUV, val layer: BlockRenderLayer) : BlockPartFace(cullFace, tintIndex, texture, blockFaceUV)

internal class LibLibBlockPartFaceDeserializer : JsonDeserializer<BlockPartFace> {
    @Throws(JsonParseException::class)
    override fun deserialize(p_deserialize_1_: JsonElement, p_deserialize_2_: Type, p_deserialize_3_: JsonDeserializationContext): LibLibBlockPartFace {
        val jsonobject = p_deserialize_1_.asJsonObject
        val enumfacing = this.parseCullFace(jsonobject)
        val i = this.parseTintIndex(jsonobject)
        val s = this.parseTexture(jsonobject)
        val layer = this.parseLayer(jsonobject)
        val blockfaceuv = p_deserialize_3_.deserialize<Any>(jsonobject, BlockFaceUV::class.java) as BlockFaceUV
        return LibLibBlockPartFace(enumfacing, i, s, blockfaceuv, layer)
    }

    private fun parseTintIndex(jsonObject: JsonObject): Int {
        return JsonUtils.getInt(jsonObject, "tintindex", -1)
    }

    private fun parseTexture(jsonObject: JsonObject): String {
        return JsonUtils.getString(jsonObject, "texture")
    }

    private fun parseCullFace(jsonObject: JsonObject): EnumFacing? {
        val s = JsonUtils.getString(jsonObject, "cullface", "")
        return EnumFacing.byName(s)
    }

    private fun parseLayer(jsonObject: JsonObject): BlockRenderLayer {
        val s = JsonUtils.getString(jsonObject, "layer", "solid")
        return try {
            BlockRenderLayer.valueOf(s.toUpperCase())
        } catch (e: IllegalArgumentException) {
            BlockRenderLayer.SOLID
        }
    }
}
