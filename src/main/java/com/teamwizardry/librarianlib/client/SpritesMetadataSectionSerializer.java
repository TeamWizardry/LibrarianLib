package com.teamwizardry.librarianlib.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.teamwizardry.librarianlib.client.SpritesMetadataSection.SpriteDefinition;

public class SpritesMetadataSectionSerializer extends BaseMetadataSectionSerializer<SpritesMetadataSection>{

	@Override
	public String getSectionName() {
		return "spritesheet";
	}
	
	private SpriteDefinition parseSprite(String name, JsonElement element) {
		if(element.isJsonArray()) {
			// uv/wh
			JsonArray arr = JsonUtils.getJsonArray(element, "spritesheet{sprites{" + name);
			if(arr.size() < 4)
				throw new JsonSyntaxException("expected spritesheet{sprites{" + name + " to have a length of 4 or higher, was " + arr.toString());
			int u = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{" + name + "[0]");
			int v = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{" + name + "[1]");
			int w = JsonUtils.getInt(arr.get(2), "spritesheet{sprites{" + name + "[2]");
			int h = JsonUtils.getInt(arr.get(3), "spritesheet{sprites{" + name + "[3]");
			
			// create def
			return new SpriteDefinition(name, u, v, w, h, new int[0], 0, 0);
			
		} else if(element.isJsonObject()) {
			JsonObject obj = JsonUtils.getJsonObject(element, "spritesheet{sprites{" + name);
			
			// uv/wh
			JsonArray arr = JsonUtils.getJsonArray(obj.get("pos"), "spritesheet{sprites{" + name + "{pos");
			if(arr.size() < 4)
				throw new JsonSyntaxException("expected spritesheet{sprites{" + name + " to have a length of 4 or higher, was " + arr.toString());
			int u = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{" + name + "[0]");
			int v = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{" + name + "[1]");
			int w = JsonUtils.getInt(arr.get(2), "spritesheet{sprites{" + name + "[2]");
			int h = JsonUtils.getInt(arr.get(3), "spritesheet{sprites{" + name + "[3]");
			
			// frames
			int[] frames = new int[0];
			if(obj.get("frames").isJsonArray()) {
				arr = JsonUtils.getJsonArray(obj.get("frames"), "spritesheet{sprites{" + name + "{frames");
				frames = new int[arr.size()];
				for (int i = 0; i < frames.length; i++) {
					frames[i] = JsonUtils.getInt(arr.get(i), "spritesheet{sprites{" + name + "{frames[" + i + "]");
				}
			} else {
				frames = new int[JsonUtils.getInt(obj.get("frames"), "spritesheet{sprites{" + name + "{frames")];
				for (int i = 0; i < frames.length; i++) {
					frames[i] = i;
				}
			}
			
			
			// animation offset
			int offsetU = 0, offsetV = 1; // default animates downward
			if(obj.get("offset") != null) {
				arr = JsonUtils.getJsonArray(obj.get("offset"), "spritesheet{sprites{" + name + "}{offset}");
				if(arr.size() < 2)
					throw new JsonSyntaxException("expected spritesheet{sprites{" + name + "{offset to have a length of 2, was " + arr.toString());
				offsetU = JsonUtils.getInt(arr.get(0), "spritesheet{sprites{" + name + "{offset[0]");
				offsetV = JsonUtils.getInt(arr.get(1), "spritesheet{sprites{" + name + "{offset[1]");
			}
			
			// create def
			return new SpriteDefinition(name, u, v, w, h, frames, offsetU, offsetV);
			
		} else {
			throw new JsonSyntaxException("expected spritesheet{sprites{" + name + " to be either an object or array");
		}
	}

	@Override
	public SpritesMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = JsonUtils.getJsonObject(json, "spritesheet{");
		
		int width  = JsonUtils.getInt(object.get("textureWidth"),  "spritesheet{textureWidth");
		int height = JsonUtils.getInt(object.get("textureHeight"), "spritesheet{textureHeight");
		
		JsonObject sprites = JsonUtils.getJsonObject(object.get("sprites"), "spritesheet{sprites");
		List<SpriteDefinition> definitions = new ArrayList<>();
		for (Entry<String, JsonElement> entry : sprites.entrySet()) {
			SpriteDefinition d = parseSprite(entry.getKey(), entry.getValue());
			if(d != null) {
				definitions.add(d);
			} else {
				
			}
		}
		return new SpritesMetadataSection(width, height, definitions);
	}

}
