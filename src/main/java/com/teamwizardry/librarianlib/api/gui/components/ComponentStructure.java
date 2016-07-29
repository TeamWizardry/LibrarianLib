package com.teamwizardry.librarianlib.api.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.client.multiblock.Structure;
import com.teamwizardry.librarianlib.client.multiblock.StructureRenderUtil;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentStructure extends GuiComponent<ComponentStructure> {

	public final Option<ComponentStructure, Color> color = new Option<>(Color.WHITE);
	private static int[] bufferInts;
    public Structure structure;
	
	public ComponentStructure(int posX, int posY, Structure structure) {
		super(posX, posY);
		this.structure = structure;
		initStructure();
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if(bufferInts == null)
			return;
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		
		vb.addVertexData(bufferInts);
		
		tessellator.draw();
	}
	
	public void initStructure() {
		bufferInts = null;
		if(structure == null)
			return;
        bufferInts = StructureRenderUtil.render(structure, color.getValue(this), 1);
    }

}
