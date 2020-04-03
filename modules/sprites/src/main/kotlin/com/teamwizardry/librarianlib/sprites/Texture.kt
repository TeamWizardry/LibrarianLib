package com.teamwizardry.librarianlib.sprites

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.awt.image.BufferedImage
import java.lang.ref.WeakReference

/**
 * This class represents a texture and it's size. It is mostly used to create [Sprite]
 * objects
 *
 * sprite information is stored in the texture .mcmeta file
 * ```json
 * {
 *     // spritesheet definition
 *     "spritesheet": {
 *         // required:
 *         // texture size in pixels (used for UV calculations)
 *         "size": [<w>, <h>],
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
class Texture(
    val location: ResourceLocation,
    /**
     * The logical width of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    val width: Int,
    /**
     * The logical height of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    val height: Int
) {
    lateinit var definition: SpritesheetDefinition
        internal set

    lateinit var image: BufferedImage
        private set

    private var sprites: MutableMap<String, Sprite> = mutableMapOf()
    private var colors: MutableMap<String, TextureColor> = mutableMapOf()

    init {
        synchronized(textures) {
            textures.add(WeakReference(this))
        }
        loadDefinition()
    }

    internal fun loadDefinition() {
        definition = SpritesheetLoader.getDefinition(location)
        sprites.forEach { (name, sprite) ->
            sprite.loadDefinition()
        }
    }

    internal fun getSpriteDefinition(name: String): SpriteDefinition {
        return definition.sprites.find { it.name == name } ?: SpritesheetLoader.missingnoSprite
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
    fun getSprite(name: String): Sprite {
        return sprites.getOrPut(name) { Sprite(this, name) }
    }

    /**
     * Gets the color with the specified name
     */
    fun getColor(name: String): Color {
        return colors.getOrPut(name) { TextureColor() }.color
    }

    private var blending = false
    private var textureLoaded = false

    /**
     * Enables linear blending
     */
    fun enableBlending() {
        blending = true
        if(!textureLoaded) {
            return
        }
        Client.textureManager.getTexture(location)?.also { tex ->
            tex.bindTexture()
            tex.setBlurMipmap(true, false)
        }
    }

    /**
     * Disables linear blending
     */
    fun disableBlending() {
        blending = false
        if(!textureLoaded) {
            return
        }
        Client.textureManager.getTexture(location)?.also { tex ->
            tex.bindTexture()
            tex.setBlurMipmap(false, false)
        }
    }

    /**
     * Bind this texture
     */
    fun bind() {
        Client.textureManager.bindTexture(location)
        if(!textureLoaded) {
            textureLoaded = true
            if (blending)
                enableBlending()
            else
                disableBlending()
        }
    }

    companion object {
        internal var textures = mutableListOf<WeakReference<Texture>>().synchronized()
    }

    private class TextureColor {
        var u: Int = 0
        var v: Int = 0
        var color = Color.WHITE
            internal set

        fun init(def: ColorJson) {
            this.u = def.u
            this.v = def.v
        }
    }
}
