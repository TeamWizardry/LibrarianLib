package com.teamwizardry.librarianlib.client.sprite

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
            var section = Minecraft.getMinecraft().resourceManager.getResource(loc).getMetadata<SpritesMetadataSection>("spritesheet")
            this.section = section
            if (section != null) {
                this.width = section.width
                this.height = section.height
                for (def in section.definitions) {
                    if (oldSprites.containsKey(def.name)) {
                        var oldSprite = oldSprites.get(def.name)
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

        var textures: MutableList<WeakReference<Texture>> = ArrayList()
    }

}
