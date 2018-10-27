package com.teamwizardry.librarianlib.features.sprite

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer
import net.minecraft.util.JsonUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.reflect.Type
import java.util.*

@SideOnly(Side.CLIENT)
class SpritesMetadataSectionSerializer : BaseMetadataSectionSerializer<SpritesMetadataSection>() {

    override fun getSectionName(): String {
        return "spritesheet"
    }

    private fun parseSprite(name: String, element: JsonElement): SpriteDefinition? {
        if (element.isJsonArray) {
            // uv/wh
            val arr = JsonUtils.getJsonArray(element, "spritesheet.sprites.$name")
            if (arr.size() != 4)
                throw JsonSyntaxException("expected spritesheet.sprites." + name + " to have a length of 4, was " + arr.toString())
            val u = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name[0]")
            val v = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name[1]")
            val w = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name[2]")
            val h = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name[3]")

            // create def
            return SpriteDefinition(
                name = name,
                u = u, v = v, w = w, h = h,
                frames = IntArray(0), offsetU = 0, offsetV = 0,
                minUCap = 0, minVCap = 0, maxUCap = 0, maxVCap = 0,
                hardScaleU = false, hardScaleV = false)

        } else if (element.isJsonObject) {
            val def = SpriteDefinition(name)
            val obj = JsonUtils.getJsonObject(element, "spritesheet.sprites.$name")

            // uv/wh
            var arr = JsonUtils.getJsonArray(obj.get("pos"), "spritesheet.sprites.$name.pos")
            if (arr.size() != 4)
                throw JsonSyntaxException("expected spritesheet.sprites." + name + " to have a length of 4, was " + arr.toString())
            def.u = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name[0]")
            def.v = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name[1]")
            def.w = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name[2]")
            def.h = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name[3]")

            // frames
            if (obj.get("frames") != null) {
                if (obj.get("frames").isJsonArray) {
                    arr = JsonUtils.getJsonArray(obj.get("frames"), "spritesheet.sprites.$name.frames")
                    def.frames = IntArray(arr.size()) {
                        JsonUtils.getInt(arr.get(it), "spritesheet.sprites.$name.frames[$it]")
                    }
                } else {
                    def.frames = IntArray(JsonUtils.getInt(obj.get("frames"), "spritesheet.sprites.$name.frames")) { it }
                }

                val frameTime = JsonUtils.getInt(obj, "frameTime", 1)

                def.frames = def.frames.flatMap { frame ->
                    MutableList(frameTime) { frame }
                }.toIntArray()
            }

            // animation offset
            def.offsetU = 0
            def.offsetV = def.h // default animates downward
            if (obj.get("offset") != null) {
                arr = JsonUtils.getJsonArray(obj.get("offset"), "spritesheet.sprites.$name.offset")
                if (arr.size() != 2)
                    throw JsonSyntaxException("expected spritesheet.sprites." + name + ".offset to have a length of 2, was " + arr.toString())
                def.offsetU = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name.offset[0]")
                def.offsetV = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name.offset[1]")
            }

            if(obj.get("caps") != null) {
                arr = JsonUtils.getJsonArray(obj.get("caps"), "spritesheet.sprites.$name.caps")
                if (arr.size() != 4)
                    throw JsonSyntaxException("expected spritesheet.sprites." + name + ".caps to have a length of 4, was " + arr.toString())
                def.minUCap = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name.caps[0]")
                def.minVCap = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name.caps[1]")
                def.maxUCap = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name.caps[2]")
                def.maxVCap = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name.caps[3]")
            }

            if(obj.get("hardScale") != null) {
                arr = JsonUtils.getJsonArray(obj.get("hardScale"), "spritesheet.sprites.$name.hardScale")
                if (arr.size() != 2)
                    throw JsonSyntaxException("expected spritesheet.sprites." + name + ".hardScale to have a length of 2, was " + arr.toString())
                def.hardScaleU = JsonUtils.getBoolean(arr.get(0), "spritesheet.sprites.$name.hardScale[0]")
                def.hardScaleV = JsonUtils.getBoolean(arr.get(1), "spritesheet.sprites.$name.hardScale[1]")
            }

            return def
        } else {
            throw JsonSyntaxException("expected spritesheet.sprites.$name to be either an object or array")
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SpritesMetadataSection {
        val jsonObject = JsonUtils.getJsonObject(json, "spritesheet.")

        val width = JsonUtils.getInt(jsonObject.get("textureWidth"), "spritesheet.textureWidth")
        val height = JsonUtils.getInt(jsonObject.get("textureHeight"), "spritesheet.textureHeight")

        val sprites = JsonUtils.getJsonObject(jsonObject.get("sprites"), "spritesheet.sprites")
        val definitions = ArrayList<SpriteDefinition>()
        for ((key, value) in sprites.entrySet()) {
            val d = parseSprite(key, value) ?: continue
            definitions.add(d)
        }
        return SpritesMetadataSection(width, height, definitions)
    }

}
