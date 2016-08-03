package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.mixin.gl.GlMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.librarianlib.structure.Structure;
import com.teamwizardry.librarianlib.structure.StructureRenderUtil;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ComponentStructure extends GuiComponent<ComponentStructure> {

	public final Option<ComponentStructure, Color> color = new Option<>(Color.WHITE);
	private static int[] bufferInts;
    public Structure structure;
	
	public ComponentStructure(int posX, int posY, Structure structure) {
		super(posX, posY);
		this.structure = structure;
		GlMixin.transform(this).func((c) -> new Vec3d(this.pos.x, this.pos.y, 0));
		initStructure();
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		GlStateManager.translate(this.pos.x, this.pos.y, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if(bufferInts == null)
			return;
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		
		vb.addVertexData(bufferInts);
		
		tessellator.draw();
		
		GlStateManager.translate(-this.pos.x, -this.pos.y, 0);
	}
	
	public void initStructure() {
		bufferInts = null;
		if(structure == null)
			return;
        bufferInts = StructureRenderUtil.render(structure, color.getValue(this), 1);
    }

}
