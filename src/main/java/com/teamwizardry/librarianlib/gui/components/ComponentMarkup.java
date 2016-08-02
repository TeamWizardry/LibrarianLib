package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.HandlerList;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.sprite.TextWrapper;
import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class ComponentMarkup extends GuiComponent<ComponentMarkup> {
	
	public final Option<ComponentMarkup, Integer> start = new Option<>(0);
	public final Option<ComponentMarkup, Integer> end = new Option<>(Integer.MAX_VALUE);
	
	List<MarkupElement> elements = new ArrayList<>();
	
	public ComponentMarkup(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		
		mouseClick.add((c, pos, button) -> {
			for (MarkupElement element : elements) {
				if(element.isMouseOver(pos.xi, pos.yi)) {
					element.click.fireAll((h) -> h.click());
					return true;
				}
			}
			return false;
		}); 
	}
	
	@Override
	public Vec2d relativePos(Vec2d pos) {
		return super.relativePos(pos).add(0, start.getValue(this));
	}
	
	public MarkupElement create(String text) {
		int x = 0;
		int y = 0;
		if(elements.size() > 0) {
			MarkupElement prev = elements.get(elements.size()-1);
			x = prev.endX();
			y = prev.endY();
		}
		MarkupElement element = new MarkupElement(y, x, size.xi, text);
		elements.add(element);
		return element;
	}
	
	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		int start = this.start.getValue(this);
		int end = this.end.getValue(this);
		GlStateManager.translate(0, -start, 0);
		for (MarkupElement element : elements) {
			if( ( element.posY >= start && element.posY <= end ) ||
					( element.posY + element.height() >= start && element.posY + element.height() <= end ) ||
					( element.posY <= start && element.posY+element.height() >= end ))
				element.render(element.isMouseOver(mousePos.xi, mousePos.yi));
		}
		GlStateManager.translate(0, start, 0);
	}
	
	public static class MarkupElement {
		public final Option<Boolean, String> format = new Option<>("");
		public final Option<Boolean, Color> color = new Option<>(Color.BLACK);
		public final Option<Boolean, Boolean> dropShadow = new Option<>(false);
		public final HandlerList<IClickHandler> click = new HandlerList<>();
		public int posY;
		public List<String> lines = new ArrayList<>();
		public int firstLineOffset;
		private int[] lengths;
		
		public MarkupElement(int posY, int firstLineOffset, int width, String text) {
			TextWrapper.wrap(Minecraft.getMinecraft().fontRendererObj, lines, text, firstLineOffset, width);
			this.posY = posY;
			this.firstLineOffset = firstLineOffset;
			lengths = new int[lines.size()];
			for (int i = 0; i < lengths.length; i++) {
				lengths[i] = Minecraft.getMinecraft().fontRendererObj.getStringWidth(lines.get(i));
			}
		}
		
		public void render(boolean hover) {
			int i = 0;
			for (String line : lines) {
				drawLine(line, i == 0 ? firstLineOffset : 0, posY + i*Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, hover);
				i++;
			}
		}
		
		protected void drawLine(String line, int x, int y, boolean hover) {
			Minecraft.getMinecraft().fontRendererObj.drawString(format.getValue(hover) + line, x, y, color.getValue(hover).hexARGB(), dropShadow.getValue(hover));
		}
		
		public boolean isMouseOver(int x, int y) {
			y -= posY;
			int height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
			for (int i = 0; i < lengths.length; i++) {
				int xPos = (i == 0) ? firstLineOffset : 0;
				if(y >= i*height && y < (i+1)*height &&
					x >= xPos && x < xPos+lengths[i]) {
					return true;
				}
			}
			return false;
		}
		
		public int endX() {
			return (lengths.length == 1 ? firstLineOffset : 0) + lengths[lengths.length-1];
		}
		
		public int endY() {
			return posY + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * (lines.size()-1);
		}
		
		public int height() {
			return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * lines.size();
		}

		@FunctionalInterface
		public interface IClickHandler {
			void click();
		}
	}

}
