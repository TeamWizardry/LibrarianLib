package com.teamwizardry.librarianlib.client.model.multilayer.data

import com.google.common.collect.Maps
import com.google.gson.*
import net.minecraft.client.renderer.block.model.BlockPart
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.BlockPartRotation
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.JsonUtils
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector3f
import java.lang.reflect.Type

/**
 * Created by TheCodeWarrior
 */

class LibLibBlockPart(positionFromIn: Vector3f, positionToIn: Vector3f, mapFacesIn: Map<EnumFacing, BlockPartFace>, partRotationIn: BlockPartRotation?, shadeIn: Boolean, val layer: BlockRenderLayer) : BlockPart(positionFromIn, positionToIn, mapFacesIn, partRotationIn, shadeIn)

@SideOnly(Side.CLIENT)
internal class LibLibBlockPartDeserializer : JsonDeserializer<BlockPart> {
    @Throws(JsonParseException::class)
    override fun deserialize(p_deserialize_1_: JsonElement, p_deserialize_2_: Type, p_deserialize_3_: JsonDeserializationContext): BlockPart {
        val jsonobject = p_deserialize_1_.asJsonObject
        val vector3f = this.parsePositionFrom(jsonobject)
        val vector3f1 = this.parsePositionTo(jsonobject)
        val blockpartrotation = this.parseRotation(jsonobject)
        val map = this.parseFacesCheck(p_deserialize_3_, jsonobject)

        val layer = this.parseLayer(jsonobject)

        if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
            throw JsonParseException("Expected shade to be a Boolean")
        } else {
            val flag = JsonUtils.getBoolean(jsonobject, "shade", true)
            return LibLibBlockPart(vector3f, vector3f1, map, blockpartrotation, flag, layer)
        }
    }

    private fun parseRotation(jsonObject: JsonObject): BlockPartRotation? {
        var blockpartrotation: BlockPartRotation? = null

        if (jsonObject.has("rotation")) {
            val jsonobject = JsonUtils.getJsonObject(jsonObject, "rotation")
            val vector3f = this.parsePosition(jsonobject, "origin")
            vector3f.scale(0.0625f)
            val axis = this.parseAxis(jsonobject)
            val f = this.parseAngle(jsonobject)
            val flag = JsonUtils.getBoolean(jsonobject, "rescale", false)
            blockpartrotation = BlockPartRotation(vector3f, axis, f, flag)
        }

        return blockpartrotation
    }

    private fun parseAngle(jsonObject: JsonObject): Float {
        val f = JsonUtils.getFloat(jsonObject, "angle")

        if (f != 0.0f && MathHelper.abs(f) != 22.5f && MathHelper.abs(f) != 45.0f) {
            throw JsonParseException("Invalid rotation $f found, only -45/-22.5/0/22.5/45 allowed")
        } else {
            return f
        }
    }

    private fun parseAxis(jsonObject: JsonObject): EnumFacing.Axis {
        val s = JsonUtils.getString(jsonObject, "axis")
        val axis = EnumFacing.Axis.byName(s.toLowerCase())

        if (axis == null) {
            throw JsonParseException("Invalid rotation axis: " + s)
        } else {
            return axis
        }
    }

    private fun parseFacesCheck(deserializationContext: JsonDeserializationContext, jsonObject: JsonObject): Map<EnumFacing, BlockPartFace> {
        val map = this.parseFaces(deserializationContext, jsonObject)

        if (map.isEmpty()) {
            throw JsonParseException("Expected between 1 and 6 unique faces, got 0")
        } else {
            return map
        }
    }

    private fun parseFaces(deserializationContext: JsonDeserializationContext, jsonObject: JsonObject): Map<EnumFacing, BlockPartFace> {
        val map = Maps.newEnumMap<EnumFacing, BlockPartFace>(EnumFacing::class.java)
        val jsonobject = JsonUtils.getJsonObject(jsonObject, "faces")

        for ((key, value) in jsonobject.entrySet()) {
            val enumfacing = this.parseEnumFacing(key as String)
            map.put(enumfacing, deserializationContext.deserialize<Any>(value as JsonElement, BlockPartFace::class.java) as BlockPartFace)
        }

        return map
    }

    private fun parseEnumFacing(name: String): EnumFacing {
        val enumfacing = EnumFacing.byName(name)

        if (enumfacing == null) {
            throw JsonParseException("Unknown facing: " + name)
        } else {
            return enumfacing
        }
    }

    private fun parsePositionTo(jsonObject: JsonObject): Vector3f {
        val vector3f = this.parsePosition(jsonObject, "to")

        if (vector3f.x >= -16.0f && vector3f.y >= -16.0f && vector3f.z >= -16.0f && vector3f.x <= 32.0f && vector3f.y <= 32.0f && vector3f.z <= 32.0f) {
            return vector3f
        } else {
            throw JsonParseException("\'to\' specifier exceeds the allowed boundaries: $vector3f")
        }
    }

    private fun parsePositionFrom(jsonObject: JsonObject): Vector3f {
        val vector3f = this.parsePosition(jsonObject, "from")

        if (vector3f.x >= -16.0f && vector3f.y >= -16.0f && vector3f.z >= -16.0f && vector3f.x <= 32.0f && vector3f.y <= 32.0f && vector3f.z <= 32.0f) {
            return vector3f
        } else {
            throw JsonParseException("\'from\' specifier exceeds the allowed boundaries: $vector3f")
        }
    }

    private fun parsePosition(jsonObject: JsonObject, memberName: String): Vector3f {
        val jsonarray = JsonUtils.getJsonArray(jsonObject, memberName)

        if (jsonarray.size() != 3) {
            throw JsonParseException("Expected 3 " + memberName + " values, found: " + jsonarray.size())
        } else {
            val afloat = FloatArray(3)

            for (i in afloat.indices) {
                afloat[i] = JsonUtils.getFloat(jsonarray.get(i), "$memberName[$i]")
            }

            return Vector3f(afloat[0], afloat[1], afloat[2])
        }
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
