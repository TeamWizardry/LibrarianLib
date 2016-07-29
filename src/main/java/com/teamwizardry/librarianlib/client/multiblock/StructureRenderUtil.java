package com.teamwizardry.librarianlib.client.multiblock;

import java.nio.IntBuffer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.api.util.block.BlockRenderUtils;
import com.teamwizardry.librarianlib.api.util.misc.Color;

public class StructureRenderUtil {

	private static VertexBuffer blockBuf = new VertexBuffer(50000);
	
	public static int[] render(Structure structure, Color color, float brightness) {
		IBlockAccess access = structure.getBlockAccess();
		
		blockBuf.reset();
        blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

        // solid block first
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
            if (state.getRenderType() == EnumBlockRenderType.INVISIBLE)
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.SOLID)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a);
        }

        // cutout block next
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
        	if (state.getRenderType() == EnumBlockRenderType.INVISIBLE)
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT && state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT_MIPPED)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a);
        }

        // translucent block next
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
        	if (state.getRenderType() == EnumBlockRenderType.INVISIBLE)
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.TRANSLUCENT)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a);
        }
        
        blockBuf.finishDrawing();

        IntBuffer intBuf = blockBuf.getByteBuffer().asIntBuffer();
        int[] bufferInts = new int[intBuf.limit()];
        for (int i = 0; i < bufferInts.length; i++) {
            bufferInts[i] = intBuf.get(i);
        }
        return bufferInts;
	}
	
}
