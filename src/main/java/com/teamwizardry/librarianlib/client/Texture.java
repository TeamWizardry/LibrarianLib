package com.teamwizardry.librarianlib.client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableMap;
import com.teamwizardry.librarianlib.client.SpritesMetadataSection.SpriteDefinition;

/**
 * This class represents a texture and it's size. It is mostly used to create {@link Sprite}
 * objects
 */
public class Texture {

	public static List<WeakReference<Texture>> textures = new ArrayList<>();
	
	private int width;
	private int height;
	private final ResourceLocation loc;
	private SpritesMetadataSection section;
	private Map<String, Sprite> sprites;
	
	public Texture(ResourceLocation loc) {
		textures.add(new WeakReference<>(this));
		
		this.loc = loc;
		loadSpriteData();
	}
	
	/**
	 * Loads the sprite data from disk
	 */
	public void loadSpriteData() {
		Map<String, Sprite> oldSprites = this.sprites == null ? ImmutableMap.of() : this.sprites;
		this.sprites = new HashMap<>();
		this.width = this.height = 16;
		try {
			this.section = (SpritesMetadataSection) Minecraft.getMinecraft().getResourceManager().getResource(loc).getMetadata("spritesheet");
			if(section != null) {
				this.width = section.width;
				this.height = section.height;
				for (SpriteDefinition def : section.definitions) {
					if(oldSprites.containsKey(def.name)) {
						oldSprites.get(def.name).init(def.u, def.v, def.w, def.h, def.frames, def.offsetU, def.offsetV);
						sprites.put(def.name, oldSprites.get(def.name));
					} else {
						sprites.put(def.name, new Sprite(this, def.u, def.v, def.w, def.h, def.frames, def.offsetU, def.offsetV));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the sprite with the specified name
	 */
	public Sprite getSprite(String name, int w, int h) {
		Sprite s = sprites.get(name);
		if(s == null) {
			// create a new one each time so on reload it'll exist and be replaced with a real one
			s = new Sprite(this, 0, 0, this.width, this.height, new int[0], 0, 0);
			sprites.put(name, s);
		}
		
		s.width = w;
		s.height = h;
		return s;
	}
	
	/**
	 * The width of the texture in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * The height of the texture in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * The location of the texture
	 */
	public ResourceLocation getLoc() {
		return loc;
	}
	
	/**
	 * Bind this texture
	 */
	public void bind() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(getLoc());
	}
	
}
