package com.teamwizardry.librarianlib.client;

import java.util.List;

import net.minecraft.client.resources.data.IMetadataSection;

public class SpritesMetadataSection implements IMetadataSection {

	public List<SpriteDefinition> definitions;
	public int width, height;
	
	public SpritesMetadataSection(int width, int height, List<SpriteDefinition> definitions) {
		super();
		this.width = width;
		this.height = height;
		this.definitions = definitions;
	}

	public static class SpriteDefinition {
		public String name;
		public int u, v, w, h;
		public int[] frames;
		
		public SpriteDefinition(String name, int u, int v, int w, int h, int[] animationTimes) {
			super();
			this.name = name;
			this.u = u;
			this.v = v;
			this.w = w;
			this.h = h;
			this.frames = animationTimes;
		}
	}
	
}
