package com.teamwizardry.librarianlib.features.sprite

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer
import net.minecraft.util.JsonUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.lang.reflect.Type
import java.util.*

@SideOnly(Side.CLIENT)
class SpritesMetadataSectionSerializer : BaseMetadataSectionSerializer<SpritesMetadataSection>() {

    override fun getSectionName(): String {
        return "spritesheet"
    }

    private fun parseSprite(name: String, element: JsonElement): SpriteDefinition? {
        val def = SpriteDefinition(name)
        if (element.isJsonArray) {
            // uv/wh
            val arr = JsonUtils.getJsonArray(element, "spritesheet.sprites.$name")
            if (arr.size() != 4)
                throw JsonSyntaxException("expected spritesheet.sprites.$name to have a length of 4, was $arr")
            def.u = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name[0]")
            def.v = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name[1]")
            def.w = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name[2]")
            def.h = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name[3]")

        } else if (element.isJsonObject) {
            val obj = JsonUtils.getJsonObject(element, "spritesheet.sprites.$name")

            // uv/wh
            var arr = JsonUtils.getJsonArray(obj.get("pos"), "spritesheet.sprites.$name.pos")
            if (arr.size() != 4)
                throw JsonSyntaxException("expected spritesheet.sprites.$name to have a length of 4, was $arr")
            def.u = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name.pos[0]")
            def.v = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name.pos[1]")
            def.w = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name.pos[2]")
            def.h = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name.pos[3]")

            // frames
            val framesElement = obj.get("frames")
            if (framesElement != null) {
                if (framesElement.isJsonArray) {
                    arr = JsonUtils.getJsonArray(framesElement, "spritesheet.sprites.$name.frames")
                    def.frames = IntArray(arr.size()) {
                        JsonUtils.getInt(arr.get(it), "spritesheet.sprites.$name.frames[$it]")
                    }
                } else {
                    val count = JsonUtils.getInt(framesElement, "spritesheet.sprites.$name.frames")
                    def.frames = IntArray(count) { it }
                }

                val frameTime = JsonUtils.getInt(obj, "frameTime", 1)

                def.frames = def.frames.flatMap { frame ->
                    MutableList(frameTime) { frame }
                }.toIntArray()
            }

            // animation offset
            def.offsetU = 0
            def.offsetV = def.h // default animates downward
            val offsetElement = obj.get("offset")
            if (offsetElement != null) {
                arr = JsonUtils.getJsonArray(offsetElement, "spritesheet.sprites.$name.offset")
                if (arr.size() != 2)
                    throw JsonSyntaxException("expected spritesheet.sprites.$name.offset to have a length of 2, was $arr")
                def.offsetU = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name.offset[0]")
                def.offsetV = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name.offset[1]")
            }

            val capsElement = obj.get("caps")
            if(capsElement != null) {
                arr = JsonUtils.getJsonArray(capsElement, "spritesheet.sprites.$name.caps")
                if (arr.size() != 4)
                    throw JsonSyntaxException("expected spritesheet.sprites.$name.caps to have a length of 4, was $arr")
                def.minUCap = JsonUtils.getInt(arr.get(0), "spritesheet.sprites.$name.caps[0]")
                def.minVCap = JsonUtils.getInt(arr.get(1), "spritesheet.sprites.$name.caps[1]")
                def.maxUCap = JsonUtils.getInt(arr.get(2), "spritesheet.sprites.$name.caps[2]")
                def.maxVCap = JsonUtils.getInt(arr.get(3), "spritesheet.sprites.$name.caps[3]")
            }

            val pinElement = obj.get("pinEdges")
            if(pinElement != null) {
                arr = JsonUtils.getJsonArray(pinElement, "spritesheet.sprites.$name.pinEdges")
                when(arr.size()) {
                    2 -> {
                        def.pinLeft = JsonUtils.getBoolean(arr.get(0), "spritesheet.sprites.$name.pinEdges[0]")
                        def.pinRight = def.pinLeft
                        def.pinTop = JsonUtils.getBoolean(arr.get(1), "spritesheet.sprites.$name.pinEdges[1]")
                        def.pinBottom = def.pinTop
                    }
                    4 -> {
                        def.pinLeft = JsonUtils.getBoolean(arr.get(0), "spritesheet.sprites.$name.pinEdges[0]")
                        def.pinTop = JsonUtils.getBoolean(arr.get(1), "spritesheet.sprites.$name.pinEdges[1]")
                        def.pinRight = JsonUtils.getBoolean(arr.get(2), "spritesheet.sprites.$name.pinEdges[2]")
                        def.pinBottom = JsonUtils.getBoolean(arr.get(3), "spritesheet.sprites.$name.pinEdges[3]")
                    }
                    else -> throw JsonSyntaxException("expected spritesheet.sprites.$name.pinEdges to have a length of 2 or 4, was $arr")
                }
            }
        } else {
            throw JsonSyntaxException("expected spritesheet.sprites.$name to be either an object or array")
        }

        return def
    }

    private fun parseColor(name: String, element: JsonElement): ColorDefinition? {
        val arr = JsonUtils.getJsonArray(element, "spritesheet.colors.$name")
        if (arr.size() != 2)
            throw JsonSyntaxException("expected spritesheet.colors.$name to have a length of 2, was $arr")
        val u = JsonUtils.getInt(arr.get(0), "spritesheet.colors.$name[0]")
        val v = JsonUtils.getInt(arr.get(1), "spritesheet.colors.$name[1]")

        // create def
        return ColorDefinition(name = name, u = u, v = v)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SpritesMetadataSection {
        val jsonObject = JsonUtils.getJsonObject(json, "spritesheet.")

        val width = JsonUtils.getInt(jsonObject.get("textureWidth"), "spritesheet.textureWidth")
        val height = JsonUtils.getInt(jsonObject.get("textureHeight"), "spritesheet.textureHeight")

        val spriteJson = jsonObject.get("sprites")?.let { JsonUtils.getJsonObject(it, "spritesheet.sprites") }
        val sprites = spriteJson?.entrySet()?.mapNotNull { (name, value) -> parseSprite(name, value) } ?: emptyList()
        val colorJson = jsonObject.get("colors")?.let { JsonUtils.getJsonObject(it, "spritesheet.colors") }
        val colors = colorJson?.entrySet()?.mapNotNull { (name, value) -> parseColor(name, value) } ?: emptyList()
        return SpritesMetadataSection(width, height, sprites, colors)
    }

}
