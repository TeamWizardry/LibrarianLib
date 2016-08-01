package com.teamwizardry.librarianlib.common.network.data;

import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import scala.actors.migration.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataNodeParsers {

	public static IBlockState parseBlockState(DataNode data) {
		IBlockState iblockstate = Blocks.AIR.getDefaultState();
		
		if (data.isMap() && data.get("id").isString()) {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(data.get("id").asStringOr("minecraft:bedrock")));
            iblockstate = block.getDefaultState();

            if (data.get("props").isMap()) {
            	Map<String, DataNode> map = data.get("props").asMap();
            	
                BlockStateContainer blockstatecontainer = block.getBlockState();

                for (String s : map.keySet()) {
                    IProperty<?> iproperty = blockstatecontainer.getProperty(s);

                    if (iproperty != null) {
                    	iblockstate = withProperty(iblockstate, iproperty, map.get(s).asString());
                    }
                }
            }

        }
		return iblockstate;
	}

	private static <T extends Comparable<T>> IBlockState withProperty(IBlockState p_190007_0_, IProperty<T> p_190007_1_, String p_190007_2_) {
        return p_190007_0_.withProperty(p_190007_1_, p_190007_1_.parseValue(p_190007_2_).get());
    }
	
    public static Item parseItem(DataNode node) {
    	if(node.isString())
    		return Item.getByNameOrId(node.asString());
	    return null;
    }
    
    public static ItemStack parseStack(DataNode node) {
    	if(node.isString())
    		return new ItemStack(parseItem(node));
	    if(node.isMap()) {
		    int meta = node.get("meta").asIntOr(0);
		    int size = node.get("amount").asIntOr(1);
		    
		    return new ItemStack(parseItem(node.get("item")), size, meta);
	    }
	    return null;
    }
    
    private static Map<String, Texture> textures = new HashMap<>();
    
	private static Pattern spritePattern = Pattern.compile("([^\\s@]+)" + "(?:@([^\\s\\[]+)" + "\\s*" + "(\\[\\s*" + "(\\d+)\\s*,\\s*(\\d+)" + "\\s*\\])?)?");
	
    public static Sprite parseSprite(DataNode node) {
    	String rl = null, sprite = null;
	    int w = 16, h = 16;
	    
    	if(node.isString()) {
    		Matcher m = spritePattern.matcher(node.asString());
		    
		    if(!m.find()) {
		    	rl = node.asString();
		    } else {
		    	rl = m.group(1);
			    sprite = m.group(2);
			    if(m.group(3) != null) {
				    w = Integer.parseInt( m.group(4) );
				    h = Integer.parseInt( m.group(5) );
			    }
		    }
	    }
	    if(node.isMap()) {
	    	rl = node.get("loc").asString();
		    sprite = node.get("sprite").asString();
		    w = node.get("width").asIntOr(w);
		    h = node.get("height").asIntOr(h);
	    }
	    
	    if(rl == null)
		    return null;
	    if(sprite == null)
	    	return new Sprite(new ResourceLocation(rl));
	    
	    Texture texture = textures.get(rl);
	    if (texture == null) {
		    texture = new Texture(new ResourceLocation(rl));
		    textures.put(rl, texture);
	    }
	    
	    return texture.getSprite(sprite, w, h);
    }
}
