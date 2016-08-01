package com.teamwizardry.librarianlib.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;

/**
 * Used to access the block in a template. Made for getActualState()
 *
 * @author Pierce Corcoran
 */
public class TemplateBlockAccess implements IBlockAccess {

    protected Template template;
    protected List<BlockInfo> templateBlocks;
    protected Map<BlockPos, IBlockState> overrides = new HashMap<>();
    
    public TemplateBlockAccess(Template template) {
        this.template = template;
        try {
			templateBlocks = (List<BlockInfo>) ReflectionHelper.findField(Template.class, "blocks", "field_186270_a").get(template);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
    }

    public void setBlockState(BlockPos pos, IBlockState state) {
    	overrides.put(pos, state);
    }
    
    public void resetSetBlocks() {
    	overrides.clear();
    }
    
    public IBlockState getBlockStateOrNull(BlockPos pos) {
    	if (overrides.containsKey(pos) && overrides.get(pos) != null)
    		return overrides.get(pos);
    	
        if (template == null || templateBlocks == null)
            return null;
        
        IBlockState state = null;
        for (BlockInfo info : templateBlocks) {
            if (info.pos.equals(pos)) {
                state = info.blockState;
                break;
            }
        }
        
        return state;
    }
    
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
    	IBlockState state = getBlockStateOrNull(pos);
        return state == null ? Blocks.AIR.getDefaultState() : state;
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return getBlockState(pos) == Blocks.AIR.getDefaultState();
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.CUSTOMIZED;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        if (template == null || templateBlocks == null)
            return _default;
        IBlockState state = getBlockStateOrNull(pos);
        
        if (state == null)
            return _default;
        return state.isSideSolid(this, pos, side);
    }

	@Override
	public Biome getBiome(BlockPos pos) {
		return Biomes.PLAINS;
	}

}
