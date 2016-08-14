package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheCodeWarrior
 */
public class ComponentRect extends GuiComponent<ComponentRect> {
	
	public final Option<ComponentRect, Color> color = new Option<>(Color.WHITE);
	
	public ComponentRect(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
	}
	
	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		Color c = color.getValue(this);
		GlStateManager.pushAttrib();
		GlStateManager.color(c.r, c.g, c.b, c.a);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		vb.pos(getPos().x,             getPos().y,             0).endVertex();
		vb.pos(getPos().x,             getPos().y+getSize().y, 0).endVertex();
		vb.pos(getPos().x+getSize().x, getPos().y+getSize().y, 0).endVertex();
		vb.pos(getPos().x+getSize().x, getPos().y,             0).endVertex();
		tessellator.draw();
		
		GlStateManager.popAttrib();
	}
}
