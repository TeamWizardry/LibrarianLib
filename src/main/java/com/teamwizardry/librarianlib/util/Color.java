package com.teamwizardry.librarianlib.util;

import net.minecraft.client.renderer.GlStateManager;

public class Color {

	public static final Color BLACK	= Color.rgb(0x000000);
	public static final Color WHITE	= Color.rgb(0xFFFFFF);
	public static final Color RED 	= Color.rgb(0xFF0000);
	public static final Color GREEN	= Color.rgb(0x00FF00);
	public static final Color BLUE 	= Color.rgb(0x0000FF);

    public final float r, g, b, a;
    public final float h, s, v;
    
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        float[] hsv = new float[3];
        java.awt.Color.RGBtoHSB((int)(r*255), (int)(g*255), (int)(b*255), hsv);
        this.h = hsv[0];
        this.s = hsv[1];
        this.v = hsv[2];
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
        float[] hsv = new float[3];
        java.awt.Color.RGBtoHSB((int)(r*255), (int)(g*255), (int)(b*255), hsv);
        this.h = hsv[0];
        this.s = hsv[1];
        this.v = hsv[2];
    }

    public static Color argb(int color) {
        float a = ((color >> 24) & 0xff) / 255f;
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;
        return new Color(r, g, b, a);
    }

    public static Color rgba(int color) {
        float r = ((color >> 24) & 0xff) / 255f;
        float g = ((color >> 16) & 0xff) / 255f;
        float b = ((color >> 8) & 0xff) / 255f;
        float a = ((color) & 0xff) / 255f;
        return new Color(r, g, b, a);
    }

    public static Color rgb(int color) {
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;
        return new Color(r, g, b);
    }

    public void glColor() {
        GlStateManager.color(r, g, b, a);
    }
    
    public int hexRGBA() {
    	return ((int)(r*255) << 24) | ((int)(g*255) << 16) | ((int)(b*255) << 8) | (int)(a*255);
    }
    
    public int hexARGB() {
    	return ((int)(a*255) << 24) | ((int)(r*255) << 16) | ((int)(g*255) << 8) | (int)(b*255);
    }

}
