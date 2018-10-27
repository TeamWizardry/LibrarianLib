package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.PngSizeInfo
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

/**
 * This class represents a texture and it's size. It is mostly used to create [Sprite]
 * objects
 *
 * sprite information stored in the texture .mcmeta file
 * ```
 * {
 *     "spritesheet": {
 *         "textureWidth": &lt;texture width in pixels&gt;,
 *         "textureHeight": &lt;texture height in pixels&gt;,
 *         "sprites": {
 *             "&lt;sprite name&gt;": [&lt;u&gt;, &lt;v&gt;, &lt;w&gt;, &lt;h&gt;],       // static sprite
 *             "&lt;sprite name&gt;": {                           // animated sprite
 *                 "pos": [&lt;u&gt;, &lt;v&gt;, &lt;w&gt;, &lt;h&gt;],
 *                 "frames": 12,                            // the number of frames, shorthand for [0, 1, 2, ..., n-1]
 *                 "frameTime": 2                           // default: 1 - the number of ticks per frame
 *             },
 *             "&lt;sprite name&gt;": {
 *                 "pos": [&lt;u&gt;, &lt;v&gt;, &lt;w&gt;, &lt;h&gt;],
 *                 "frames": [0, 1, 2, 3, 2, 1],            // animation frame indices
 *                 "offset": [&lt;u&gt;, &lt;v&gt;]                     // default: [u, &lt;h&gt;] - uv offset per frame.
 *             },
 *             "&lt;sprite name&gt;": {
 *                 "pos": [&lt;u&gt;, &lt;v&gt;, &lt;w&gt;, &lt;h&gt;],
 *                 "caps": [&lt;minU&gt;, &lt;minV&gt;, &lt;maxU&gt;, &lt;maxV&gt;], // the number of pixels on each edge that should remain non-distorted when stretching the sprite
 *                 "hardScale": [&lt;uAxis&gt;, &lt;vAxis&gt;] // default: [false, false] - whether the texture should be repeated/truncated rather than stretched/squished along each axis
 *             }
 *         }
 *     }
 * }
 * ```
 */
@SideOnly(Side.CLIENT)
class Texture(
    /**
     * The location of the texture
     */
    val loc: ResourceLocation,
    /**
     * The logical width of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    val logicalWidth: Int,
    /**
     * The logical height of this texture in pixels. Used to determine the scaling factor from texture pixels to
     * logical pixels
     */
    val logicalHeight: Int
) {
    /**
     * The width of the texture in pixels
     */
    var width: Int = 0
        private set
    /**
     * The height of the texture in pixels
     */
    var height: Int = 0
        private set

    /**
     * The ratio of texture pixels / logical pixels. Calculated by averaging the ratio on each axis.
     */
    val logicalScale: Int
        get() = ((logicalWidth.toFloat() / width + logicalHeight.toFloat() / height) / 2).toInt()
    private var section: SpritesMetadataSection? = null
    private var sprites: MutableMap<String, Sprite> = mutableMapOf()

    init {
        textures.add(WeakReference(this))
        if (SpritesMetadataSection.registered)
            loadSpriteData()
    }

    /**
     * Loads the sprite data from disk
     */
    fun loadSpriteData() {
        val oldSprites = this.sprites
        this.sprites = mutableMapOf()

        val pngSizeInfo = PngSizeInfo.makeFromResource(Minecraft().resourceManager.getResource(loc))
        var pngWidth = pngSizeInfo.pngWidth
        var pngHeight = pngSizeInfo.pngHeight

        if (width > 0 && height <= 0) {
            pngWidth = width
            pngHeight = pngHeight * width / pngWidth
        } else if (width <= 0 && height > 0) {
            pngHeight = height
            pngWidth = pngWidth * height / pngHeight
        } else if (width > 0 && height > 0) {
            pngWidth = width
            pngHeight = height
        }

        this.width = pngWidth
        this.height = pngHeight
        try {
            val section = Minecraft.getMinecraft().resourceManager.getResource(loc).getMetadata<SpritesMetadataSection>("spritesheet")
            this.section = section
            if (section != null) {
                this.width = section.width
                this.height = section.height
                for (def in section.definitions) {
                    if (oldSprites.containsKey(def.name)) {
                        val oldSprite = oldSprites[def.name]
                        if (oldSprite != null) {
                            oldSprite.init(def)
                            sprites[def.name] = oldSprite
                        }
                    } else {
                        sprites[def.name] = Sprite(this, def)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Gets the sprite with the specified name
     */
    fun getSprite(name: String): Sprite {
        var s: Sprite? = sprites[name]
        if (s == null) {
            // create a new one each time so on reload it'll exist and be replaced with a real one
            s = Sprite(this, SpriteDefinition(""))
            sprites[name] = s
        }

        return s
    }

    /**
     * Bind this texture
     */
    fun bind() {
        Minecraft.getMinecraft().textureManager.bindTexture(loc)
    }

    companion object {

        internal fun register() {
            ClientRunnable.registerReloadHandler {
                val newList = ArrayList<WeakReference<Texture>>()

                for (tex in Texture.textures) {
                    tex.get()?.loadSpriteData()
                    if (tex.get() != null) newList.add(tex)
                }

                Texture.textures = newList
            }
        }

        var textures: MutableList<WeakReference<Texture>> = ArrayList()
    }

}
