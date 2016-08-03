package com.teamwizardry.librarianlib.gui.components;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;
import java.util.function.Function;

public class ComponentText extends GuiComponent<ComponentText> {
	
	/**
	 * The text to draw
	 */
    public final Option<ComponentText, String> text = new Option<>("-NULL TEXT-");
	/**
	 * The color of the text
	 */
    public final Option<ComponentText, Color> color = new Option<>(Color.BLACK);
	/**
	 * The wrap width in pixels, -1 for no wrapping
	 */
	public final Option<ComponentText, Integer> wrap = new Option<>(-1);
	/**
	 * Whether to set the font renderer's unicode and bidi flags
	 */
	public final Option<ComponentText, Boolean> unicode = new Option<>(false);
	/**
	 * Whether to render a shadow behind the text
	 */
	public final Option<ComponentText, Boolean> shadow = new Option<>(false);
	
    public TextAlignH horizontal;
    public TextAlignV vertical;

    public ComponentText(int posX, int posY) {
        this(posX, posY, TextAlignH.LEFT, TextAlignV.TOP);
    }

    public ComponentText(int posX, int posY, TextAlignH horizontal, TextAlignV vertical) {
        super(posX, posY);
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.color.setValue(Color.argb(0xff000000));
    }

    /**
     * Set the text value and unset the function
     */
    public ComponentText val(String str) {
        text.setValue(str);
        text.noFunc();
        return this;
    }

    /**
     * Set the callback to create the text for
     *
     * @param func
     * @return
     */
    public ComponentText func(Function<ComponentText, String> func) {
        text.func(func);
        return this;
    }

    @Override
    public void drawComponent(Vec2d mousePos, float partialTicks) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    
	    String fullText = text.getValue(this);
	    int colorHex = color.getValue(this).hexARGB();
	    boolean enableFlags = unicode.getValue(this);
	    boolean dropShadow = shadow.getValue(this);
	    
        if (enableFlags) {
	        fr.setBidiFlag(true);
	        fr.setUnicodeFlag(true);
        }
        
        int x = pos.xi;
        int y = pos.yi;
	    
	    List<String> lines = null;
	
	    int wrap = this.wrap.getValue(this);
	    if(wrap == -1) {
	    	lines = ImmutableList.of(fullText);
	    } else {
		    lines = fr.listFormattedStringToWidth(fullText, wrap);
	    }
	    
	    
	    int height = lines.size() * fr.FONT_HEIGHT;
	    if (vertical == TextAlignV.MIDDLE) {
		    y -= height / 2;
	    } else if (vertical == TextAlignV.BOTTOM) {
		    y -= height;
	    }
	    
	    int i = 0;
	    for (String line : lines) {
	    	
	    	int lineX = x;
		    int lineY = y + i * fr.FONT_HEIGHT;
		
		    int textWidth = fr.getStringWidth(line);
		    if (horizontal == TextAlignH.CENTER) {
			    lineX -= textWidth / 2;
		    } else if (horizontal == TextAlignH.RIGHT) {
			    lineX -= textWidth;
		    }
	    	
	    	fr.drawString(line, lineX, lineY, colorHex, dropShadow);
	    	
	    	i++;
	    }
    
        if (enableFlags) {
            fr.setBidiFlag(false);
            fr.setUnicodeFlag(false);
        }
    }

    @Override
    protected BoundingBox2D getContentSize() {
        int wrap = this.wrap.getValue(this);

        Vec2d size;

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

	    boolean enableFlags = unicode.getValue(this);
	    
	    if(enableFlags) {
		    fr.setUnicodeFlag(true);
		    fr.setBidiFlag(true);
	    }
	    
        if (wrap == -1) {
            size = new Vec2d(fr.getStringWidth(text.getValue(this)), fr.FONT_HEIGHT);
        } else {
            List<String> wrapped = fr.listFormattedStringToWidth(text.getValue(this), wrap);
            size = new Vec2d(wrap, wrapped.size() * fr.FONT_HEIGHT);
        }
	
	    if(enableFlags) {
		    fr.setUnicodeFlag(false);
		    fr.setBidiFlag(false);
	    }
        
        return new BoundingBox2D(Vec2d.ZERO, size);
    }

    public enum TextAlignH {
        LEFT, CENTER, RIGHT
    }

    public enum TextAlignV {
        TOP, MIDDLE, BOTTOM
    }

}
