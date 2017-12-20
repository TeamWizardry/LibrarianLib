package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
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
        val loc: ResourceLocation) {
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
        this.width = 16
        this.height = 16
        try {
            val section = Minecraft.getMinecraft().resourceManager.getResource(loc).getMetadata<SpritesMetadataSection>("spritesheet")
            this.section = section
            if (section != null) {
                this.width = section.width
                this.height = section.height
                for (def in section.definitions) {
                    if (oldSprites.containsKey(def.name)) {
                        val oldSprite = oldSprites.get(def.name)
                        if (oldSprite != null) {
                            oldSprite.init(def.u, def.v, def.w, def.h, def.frames, def.offsetU, def.offsetV)
                            sprites.put(def.name, oldSprite)
                        }
                    } else {
                        sprites.put(def.name, Sprite(this, def.u, def.v, def.w, def.h, def.frames, def.offsetU, def.offsetV))
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
    fun getSprite(name: String, w: Int, h: Int): Sprite {
        var s: Sprite? = sprites[name]
        if (s == null) {
            // create a new one each time so on reload it'll exist and be replaced with a real one
            s = Sprite(this, 0, 0, this.width, this.height, IntArray(0), 0, 0)
            sprites.put(name, s)
        }

        s.width = w
        s.height = h
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
