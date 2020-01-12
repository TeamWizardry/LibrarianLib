package com.teamwizardry.librarianlib.sprite

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.teamwizardry.librarianlib.core.util.kotlin.JsonParsingDSL
import com.teamwizardry.librarianlib.core.util.kotlin.parse
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraftforge.common.util.JsonUtils
import java.awt.Color
import java.lang.reflect.Type
import java.util.*

class SpritesMetadataSectionSerializer : IMetadataSectionSerializer<SpritesMetadataSection> {

    override fun deserialize(json: JsonObject): SpritesMetadataSection {
        return json.parse("spritesheet") {
            val width = get("textureWidth").asInt()
            val height = get("textureWidth").asInt()

            val sprites = getOrNull("sprites")?.properties?.map { (name, value) ->
                parseSprite(name, value)
            }?.toList() ?: emptyList()

            val colors = getOrNull("colors")?.properties?.map { (name, value) ->
                parseColor(name, value)
            }?.toList() ?: emptyList()

            SpritesMetadataSection(width, height, sprites, colors)
        }
    }

    override fun getSectionName(): String {
        return "spritesheet"
    }

    private fun parseSprite(name: String, element: JsonParsingDSL): SpriteDefinition {
        val def = SpriteDefinition(name)

        element {
            when {
                isArray -> {
                    expectExactSize(4)

                    def.u = get(0).asInt()
                    def.v = get(1).asInt()
                    def.w = get(2).asInt()
                    def.h = get(3).asInt()
                }
                isObject -> {
                    // ========== position/size
                    "pos" {
                        expectExactSize(4)

                        def.u = get(0).asInt()
                        def.v = get(1).asInt()
                        def.w = get(2).asInt()
                        def.h = get(3).asInt()
                    }

                    // ========== frame count/indices
                    "frames" {
                        when {
                            isArray -> def.frames = elements.map { it.asInt() }.toList().toIntArray()
                            isNumber -> def.frames = IntArray(asInt())
                            else -> throw typeError("an array or an integer")
                        }
                    }

                    // ========== animation offset
                    def.offsetU = 0
                    def.offsetV = def.h // default animates downward
                    "offset" / {
                        expectExactSize(2)
                        def.offsetU = get(0).asInt()
                        def.offsetV = get(1).asInt()
                    }

                    // ========== caps
                    "caps" / {
                        expectExactSize(4)

                        def.minUCap = get(0).asInt()
                        def.minVCap = get(1).asInt()
                        def.maxUCap = get(2).asInt()
                        def.maxVCap = get(3).asInt()
                    }

                    // ========== pinning
                    "pinEdges" / {
                        when(size()) {
                            2 -> {
                                def.pinLeft = get(0).asBoolean()
                                def.pinRight = false
                                def.pinTop = get(1).asBoolean()
                                def.pinBottom = false
                            }
                            4 -> {
                                def.pinLeft = get(0).asBoolean()
                                def.pinTop = get(1).asBoolean()
                                def.pinRight = get(2).asBoolean()
                                def.pinBottom = get(3).asBoolean()
                            }
                            else -> throw jsonError("Expected an array with exactly 2 or 4 elements.")
                        }
                    }
                }
                else -> throw typeError("an array or an object")
            }
        }

        return def
    }

    private fun parseColor(name: String, element: JsonParsingDSL): ColorDefinition {
        return element {
            expectExactSize(2)

            ColorDefinition(name = name, u = get(0).asInt(), v = get(1).asInt())
        }
    }
}
