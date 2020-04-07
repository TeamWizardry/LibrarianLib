package com.teamwizardry.librarianlib.sprites

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.util.kotlin.JsonParsingDSL
import com.teamwizardry.librarianlib.core.util.kotlin.parse
import net.minecraft.resources.data.IMetadataSectionSerializer

internal class SpritesMetadataSectionSerializer : IMetadataSectionSerializer<SpritesheetJson> {

    override fun deserialize(json: JsonObject): SpritesheetJson {
        return json.parse("spritesheet") {
            val (width, height) = get("size") {
                expectExactSize(2)
                get(0).asInt() to get(1).asInt()
            }

            val sprites = getOrNull("sprites")?.properties?.map { (name, value) ->
                parseSprite(name, value)
            }?.toList() ?: emptyList()

            val colors = getOrNull("colors")?.properties?.map { (name, value) ->
                parseColor(name, value)
            }?.toList() ?: emptyList()

            var blur = false
            var mipmap = false
            optional("display") {
                blur = getOrNull("blur")?.asBoolean() ?: blur
                mipmap = getOrNull("mipmap")?.asBoolean() ?: mipmap
            }

            SpritesheetJson(width, height, blur, mipmap, sprites, colors)
        }
    }

    override fun getSectionName(): String {
        return "spritesheet"
    }

    private fun parseSprite(name: String, element: JsonParsingDSL): SpriteJson {
        val def = SpriteJson(name)

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
                    optional("frames") {
                        when {
                            isArray -> def.frames = elements.map { it.asInt() }.toList().toIntArray()
                            isNumber -> def.frames = IntArray(asInt()) { it }
                            else -> throw typeError("an array or an integer")
                        }
                    }

                    // ========== animation offset
                    def.offsetU = 0
                    def.offsetV = def.h // default animates downward
                    optional("offset") {
                        expectExactSize(2)
                        def.offsetU = get(0).asInt()
                        def.offsetV = get(1).asInt()
                    }

                    // ========== caps
                    optional("caps") {
                        expectExactSize(4)

                        def.minUCap = get(0).asInt()
                        def.minVCap = get(1).asInt()
                        def.maxUCap = get(2).asInt()
                        def.maxVCap = get(3).asInt()
                    }

                    // ========== pinning
                    optional("pinEdges") {
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

    private fun parseColor(name: String, element: JsonParsingDSL): ColorJson {
        return element {
            expectExactSize(2)

            ColorJson(name = name, u = get(0).asInt(), v = get(1).asInt())
        }
    }
}
