package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.rendering.DefaultRenderPhases
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * This class represents a texture and it's size. It is mostly used to create [MosaicSprite]
 * objects. If there is no mosaic mcmeta the texture will be loaded as a "raw"
 * texture, which contains a single sprite with an empty name (`""`).
 *
 * sprite information is stored in the texture .mcmeta file
 * ```json
 * {
 *     // mosaic definition
 *     "mosaic": {
 *         // required:
 *         // texture size in pixels (used for UV calculations)
 *         "size": [<w>, <h>],
 *         "blur": true, // enable texture interpolation
 *         "mipmap": true, // enable mipmapping
 *
 *         "sprites": {
 *             // static sprite shorthand
 *             "<sprite name>": [<u>, <v>, <w>, <h>],
 *
 *             "<sprite name>": {
 *                 // required:
 *                 "pos": [<u>, <v>, <w>, <h>],
 *
 *                 // the number of frames, shorthand for "frames": [0, 1, 2, ..., n-1]
 *                 "frames": 12,
 *
 *                 // animation frame indices
 *                 "frames": [0, 1, 2, 3, 2, 1],
 *
 *                 // the number of ticks per frame, defaults to 1
 *                 "frameTime": 2
 *
 *                 // uv offset per frame, defaults to [0, <h>]
 *                 "offset": [<u>, <v>]
 *
 *                 // the number of pixels on each edge that should remain non-distorted when stretching the sprite
 *                 // See: https://en.wikipedia.org/wiki/9-slice_scaling
 *                 "caps": [<minU>, <minV>, <maxU>, <maxV>],
 *
 *                 // which edges should be "pinned" when drawing the sprite larger than normal. Edges that have not
 *                 // been pinned will end up being repeated or truncated. If both pins on an axis are false, the sprite
 *                 // will default to pinning on both sides along that axis. Defaults to [true, true, true, true]
 *                 "pinEdges": [<left>, <top>, <right>, <bottom>]
 *
 *                 // shorthand for "pinEdges": [<horizontal>, <vertical>, false, false]
 *                 "pinEdges": [<horizontal>, <vertical>]
 *             }
 *         },
 *         // optional:
 *         "colors": {
 *             // create a named color based on the color of the pixel at (<u>, <v>)
 *             "<color name>": [<u>, <v>]
 *         }
 *     }
 * }
 * ```
 */
public class Mosaic(
    public val location: Identifier,
    /**
     * The logical width of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    public val width: Int,
    /**
     * The logical height of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    public val height: Int
) {
    private lateinit var definition: MosaicDefinition
    public val image: BufferedImage
        get() = definition.image

    private var sprites: MutableMap<String, MosaicSprite> = mutableMapOf()
    private var colors: MutableMap<String, TextureColor> = mutableMapOf()

    init {
        synchronized(textures) {
            textures.add(this)
        }
        loadDefinition()
    }

    internal fun loadDefinition() {
        definition = MosaicLoader.getDefinition(location)
        Client.textureManager.getTexture(location).setFilter(definition.blur, definition.mipmap)

        sprites.forEach { (_, sprite) ->
            sprite.loadDefinition()
        }
        colors.forEach { (_, color) ->
            color.loadDefinition()
        }
    }

    internal fun getSpriteDefinition(name: String): SpriteDefinition {
        if(definition.singleSprite && name != "") {
            logger.warn("Attempted to load a named sprite ('$name') from a raw texture ('$location')")
        }
        return definition.sprites.find { it.name == name } ?: MosaicLoader.missingnoSprite
    }

    internal fun getColorDefinition(name: String): ColorDefinition {
        if(definition.singleSprite && name != "") {
            logger.warn("Attempted to load a color ('$name') from a raw texture ('$location')")
        }
        return definition.colors.find { it.name == name } ?: MosaicLoader.missingnoColor
    }

    internal fun logicalU(pixels: Int): Int {
        return pixels * width / definition.uvSize.x
    }

    internal fun logicalV(pixels: Int): Int {
        return pixels * height / definition.uvSize.y
    }

    /**
     * Gets the sprite with the specified name
     */
    public fun getSprite(name: String): MosaicSprite {
        return sprites.getOrPut(name) { MosaicSprite(this, name) }
    }

    /**
     * Gets the color with the specified name
     */
    public fun getColor(name: String): Color {
        return colors.getOrPut(name) { TextureColor(this, name) }.color
    }

    @JvmSynthetic
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): SpriteDelegate {
        return SpriteDelegate(getSprite(property.name))
    }

    internal companion object {
        private val logger = LibLibMosaic.makeLogger<Mosaic>()

        @get:JvmSynthetic
        internal val textures = weakSetOf<Mosaic>().synchronized()
    }

    private class TextureColor(private val mosaic: Mosaic, val name: String) {
        lateinit var definition: ColorDefinition
            internal set

        var u: Int = 0
            private set
        var v: Int = 0
            private set
        var color: Color = Color.WHITE
            private set

        init {
            loadDefinition()
        }

        fun loadDefinition() {
            definition = mosaic.getColorDefinition(name)
            u = definition.uv.x
            v = definition.uv.y
            color = definition.color
        }
    }

    public class SpriteDelegate(private val sprite: MosaicSprite) {
        public operator fun getValue(thisRef: Any?, property: KProperty<*>): MosaicSprite = sprite
    }
}
