package com.teamwizardry.librarianlib.api.gui.components;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.api.util.misc.Color;

public class ComponentText extends GuiComponent<ComponentText> {

	public final Option<ComponentText, String> text = new Option<>("-NULL TEXT-");
	public final Option<ComponentText, Color> color = new Option<>(Color.BLACK);
	
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
	 * @param func
	 * @return
	 */
	public ComponentText func(Function<ComponentText, String> func) {
		text.func(func);
		return this;
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		
		String val = text.getValue(this);
		
		int x = pos.xi;
		int y = pos.yi;
		
		int textWidth = fr.getStringWidth(val);
		if(horizontal == TextAlignH.CENTER) {
			x -= textWidth/2;
		} else if(horizontal == TextAlignH.RIGHT) {
			x -= textWidth;
		}
		if(vertical == TextAlignV.MIDDLE) {
			y -= fr.FONT_HEIGHT/2;
		} else if(vertical == TextAlignV.BOTTOM) {
			y -= fr.FONT_HEIGHT;
		}
		
		fr.drawString(val, x, y, color.getValue(this).hexARGB());
	}
	
	public static enum TextAlignH {
		LEFT, CENTER, RIGHT;
	}
	
	public static enum TextAlignV {
		TOP, MIDDLE, BOTTOM;
	}

}
