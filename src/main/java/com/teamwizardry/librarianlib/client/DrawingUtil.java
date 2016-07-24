package com.teamwizardry.librarianlib.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

public class DrawingUtil {
    static boolean isDrawing = false;
    
    /**
     * Start drawing multiple quads to be pushed to the GPU at once
     */
    public static void startDrawingSession() {
    	Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        isDrawing = true;
    }
    
    /**
     * Finish drawing multiple quads and push them to the GPU
     */
    public static void endDrawingSession() {
    	Tessellator tessellator = Tessellator.getInstance();
    	tessellator.draw();
    	isDrawing = false;
    }
    
    /**
     * <strong>!!! Use {@link Sprite#draw(float, float)} or {@link Sprite#draw(float, float, int, int)} instead !!!</strong>
     * 
     * <p>Draw a sprite at a location with the width and height specified.</p>
     * @param sprite The sprite to draw
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    public static void draw(Sprite sprite, float x, float y, int width, int height) {
    	
    	float
    		minX = x, minY = y,
    		maxX = x+width, maxY = y+height;
    	
    	Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        if(!isDrawing)
        	vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
        vb.pos(minX, maxY, 0).tex(sprite.minU(), sprite.maxV()).endVertex();
        vb.pos(maxX, maxY, 0).tex(sprite.maxU(), sprite.maxV()).endVertex();
        vb.pos(maxX, minY, 0).tex(sprite.maxU(), sprite.minV()).endVertex();
        vb.pos(minX, minY, 0).tex(sprite.minU(), sprite.minV()).endVertex();

        if(!isDrawing)
        	tessellator.draw();
    }
    
    /**
     * <strong>!!! Use {@link Sprite#drawClipped(float, float, int, int)} instead !!!</strong>
     * 
     * <p>Draw a sprite at a location with the width and height specified by clipping or tiling instead of stretching/squishing</p>
     * @param sprite The sprite to draw
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    public static void drawClipped(Sprite sprite, float x, float y, int width, int height) {
    	Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        if(!isDrawing)
        	vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
        int wholeSpritesX = (int) Math.ceil((float)width  / (float)sprite.width )-1;
        int wholeSpritesY = (int) Math.ceil((float)height / (float)sprite.height)-1;
        
        int leftoverWidth  = width  % sprite.width;
        int leftoverHeight = height % sprite.height;
        
        if(leftoverWidth == 0)
        	leftoverWidth = sprite.width;
        if(leftoverHeight == 0)
        	leftoverHeight = sprite.height;
        
        for (int xIndex = 0; xIndex <= wholeSpritesX; xIndex++) {
        	for (int yIndex = 0; yIndex <= wholeSpritesY; yIndex++) {
        		
    			boolean smallX = xIndex == wholeSpritesX;
    			boolean smallY = yIndex == wholeSpritesY;
        		
        		int spriteWidth = smallX ? wholeSpritesX == 0 ? width : leftoverWidth : sprite.width;
        		int spriteHeight = smallY ? wholeSpritesY == 0 ? height : leftoverHeight : sprite.height;
        		int offsetX = sprite.width*xIndex;
        		int offsetY = sprite.height*yIndex;
        		
        		float
	        		minX = x + offsetX, minY = y + offsetY,
	        		maxX = minX + spriteWidth, maxY = minY + spriteHeight;
	        	float
	        		uSpan = sprite.maxU() - sprite.minU(), vSpan = sprite.maxV() - sprite.minV(),
	        		
	        		minU = sprite.minU(), minV = sprite.minV(),
	        		maxU = minU + uSpan*((float)spriteWidth/(float)sprite.width),
	        		maxV = minV + vSpan*((float)spriteHeight/(float)sprite.height);
        		
	            vb.pos(minX, maxY, 0).tex(minU, maxV).endVertex();
	            vb.pos(maxX, maxY, 0).tex(maxU, maxV).endVertex();
	            vb.pos(maxX, minY, 0).tex(maxU, minV).endVertex();
	            vb.pos(minX, minY, 0).tex(minU, minV).endVertex();
        		
    		}
		}

        if(!isDrawing)
        	tessellator.draw();
    }
}
