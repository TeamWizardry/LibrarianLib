package com.teamwizardry.librarianlib.features.sprite

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.teamwizardry.librarianlib.features.sprite.SpritesMetadataSection.SpriteDefinition
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
            val arr = JsonUtils.getJsonArray(element, "spritesheet{sprites{" + name)
            if (arr.size() < 4)
                throw JsonSyntaxException("expected spritesheet{sprites{" + name + " to have a length of 4 or higher, was " + arr.toString())
            val u = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{$name[0]")
            val v = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{$name[1]")
            val w = JsonUtils.getInt(arr.get(2), "spritesheet{sprites{$name[2]")
            val h = JsonUtils.getInt(arr.get(3), "spritesheet{sprites{$name[3]")

            // create def
            return SpriteDefinition(name, u, v, w, h, IntArray(0), 0, 0)

        } else if (element.isJsonObject) {
            val obj = JsonUtils.getJsonObject(element, "spritesheet{sprites{" + name)

            // uv/wh
            var arr = JsonUtils.getJsonArray(obj.get("pos"), "spritesheet{sprites{$name{pos")
            if (arr.size() < 4)
                throw JsonSyntaxException("expected spritesheet{sprites{" + name + " to have a length of 4 or higher, was " + arr.toString())
            val u = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{$name[0]")
            val v = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{$name[1]")
            val w = JsonUtils.getInt(arr.get(2), "spritesheet{sprites{$name[2]")
            val h = JsonUtils.getInt(arr.get(3), "spritesheet{sprites{$name[3]")

            // frames
            val frames_: IntArray
            if (obj.get("frames").isJsonArray) {
                arr = JsonUtils.getJsonArray(obj.get("frames"), "spritesheet{sprites{$name{frames")
                frames_ = IntArray(arr.size())
                for (i in frames_.indices) {
                    frames_[i] = JsonUtils.getInt(arr.get(i), "spritesheet{sprites{$name{frames[$i]")
                }
            } else {
                frames_ = IntArray(JsonUtils.getInt(obj.get("frames"), "spritesheet{sprites{$name{frames"))
                for (i in frames_.indices) {
                    frames_[i] = i
                }
            }

            val frameTime = JsonUtils.getInt(obj, "frameTime", 1)

            val frames: IntArray = IntArray(frames_.size * frameTime)

            var j = 0
            for (i in frames_) {
                for (k in 1..frameTime) {
                    frames[j] = i
                    j++
                }
            }

            // animation offset
            var offsetU = 0
            var offsetV = h // default animates downward
            if (obj.get("offset") != null) {
                arr = JsonUtils.getJsonArray(obj.get("offset"), "spritesheet{sprites{$name}{offset}")
                if (arr.size() < 2)
                    throw JsonSyntaxException("expected spritesheet{sprites{" + name + "{offset to have a length of 2, was " + arr.toString())
                offsetU = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{$name{offset[0]")
                offsetV = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{$name{offset[1]")
            }

            // create def
            return SpriteDefinition(name, u, v, w, h, frames, offsetU, offsetV)

        } else {
            throw JsonSyntaxException("expected spritesheet{sprites{$name to be either an object or array")
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SpritesMetadataSection {
        val jsonObject = JsonUtils.getJsonObject(json, "spritesheet{")

        val width = JsonUtils.getInt(jsonObject.get("textureWidth"), "spritesheet{textureWidth")
        val height = JsonUtils.getInt(jsonObject.get("textureHeight"), "spritesheet{textureHeight")

        val sprites = JsonUtils.getJsonObject(jsonObject.get("sprites"), "spritesheet{sprites")
        val definitions = ArrayList<SpriteDefinition>()
        for ((key, value) in sprites.entrySet()) {
            val d = parseSprite(key, value) ?: continue
            definitions.add(d)
        }
        return SpritesMetadataSection(width, height, definitions)
    }

}
