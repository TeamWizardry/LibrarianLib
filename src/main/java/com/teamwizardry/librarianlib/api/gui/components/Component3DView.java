package com.teamwizardry.librarianlib.api.gui.components;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.client.multiblock.Structure;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.librarianlib.math.Vec2;

public class Component3DView extends GuiComponent<Component3DView> {

	public Component3DView(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		
		preDraw.add(this::preDraw);
    	postDraw.add(this::postDraw);
	}

    Vec2 dragStart = Vec2.ZERO;
    public Vec3d offset = Vec3d.ZERO;
    public double zoom;
	public double rotX, rotY, rotZ;
    EnumMouseButton dragButton = null;
	
	@Override
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		super.mouseDown(mousePos, button);

    	if(!mouseOverThisFrame)
    		return;
		if(dragButton != null)
    		return;
    	
    	dragStart = mousePos;
    	dragButton = button;
    }
	
	@Override
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		super.mouseUp(mousePos, button);
		
    	calcDrag(mousePos, button);
    	
    	dragStart = Vec2.ZERO;
    	dragButton = null;
    }
	
	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		//noop for now
	}
	
	public void calcDrag(Vec2 mousePos, EnumMouseButton button) {
		if(button != dragButton)
    		return;
    	
    	if(dragButton == EnumMouseButton.LEFT) {
    		rotY += (mousePos.x-dragStart.x);
    		rotX += (mousePos.y-dragStart.y);
    	}
    	if(dragButton == EnumMouseButton.RIGHT) {
    		Vec3d offset = new Vec3d(mousePos.x-dragStart.x, mousePos.y-dragStart.y, 0);
        	Matrix4 matrix = new Matrix4();
        	matrix.rotate(-Math.toRadians(rotZ), new Vec3d(0, 0, 1));
        	matrix.rotate(-Math.toRadians(rotY), new Vec3d(0, 1, 0));
        	matrix.rotate(-Math.toRadians(rotX), new Vec3d(1, 0, 0));
        	matrix.scale(new Vec3d(1.0/zoom, -1.0/zoom, 1.0/zoom));
        	offset = matrix.apply(offset);
        	
        	this.offset = this.offset.add(offset);
    	}
	}
	
	public void preDraw(Component3DView _comp_, Vec2 mousePos, float ticks) {
        GlStateManager.translate(this.size.x / 2, this.size.y / 2, 500);
        
        double rotX = this.rotX;
        double rotY = this.rotY;
        double rotZ = this.rotZ;
        Vec3d offset = this.offset;
        
        calcDrag(mousePos, dragButton);
        
    	GlStateManager.enableRescaleNormal();
    	GlStateManager.scale(zoom, -zoom, zoom);
    	GlStateManager.rotate((float)this.rotX, 1, 0, 0);
    	GlStateManager.rotate((float)this.rotY, 0, 1, 0);
    	GlStateManager.rotate((float)this.rotZ, 0, 0, 1);
    	GlStateManager.translate(this.offset.xCoord, this.offset.yCoord, this.offset.zCoord);
    	
    	this.offset = offset;
    	this.rotZ = rotZ;
    	this.rotY = rotY;
    	this.rotX = rotX;
    	
    	{ // RenderHelper.enableStandardItemLighting but brighter because of different light and ambiant values.
            Vec3d LIGHT0_POS = new Vec3d(0, 1,  0.1).normalize();//(new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
            Vec3d LIGHT1_POS = new Vec3d(0, 1, -0.1).normalize();//(new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

            GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
//            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
            GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

            float light = 0.3F;
            float ambiant = 0.7F;
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.xCoord, (float) LIGHT0_POS.yCoord, (float) LIGHT0_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.xCoord, (float) LIGHT1_POS.yCoord, (float) LIGHT1_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambiant, ambiant, ambiant, 1.0F));
    	}
    	
	}
	
	public void postDraw(Component3DView _comp_, Vec2 mousePos, float ticks) {
		RenderHelper.disableStandardItemLighting();
    	GlStateManager.disableRescaleNormal();
	}

}
