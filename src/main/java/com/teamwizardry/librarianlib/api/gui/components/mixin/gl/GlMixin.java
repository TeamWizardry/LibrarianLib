package com.teamwizardry.librarianlib.api.gui.components.mixin.gl;

import net.minecraft.client.renderer.GlStateManager;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.misc.Color;

public class GlMixin {
	
	public static final String
		TAG_ATTRIB = "mixin_attrib",
		TAG_COLOR  = "mixin_color";
	
	public static void pushPopAttrib(GuiComponent<?> component) {
		if(!component.addTag(TAG_ATTRIB))
			return;
		
		component.preDraw.add((c, pos, partialTicks) -> {
			GlStateManager.pushAttrib();
		});
		component.postDraw.addFirst((c, pos, partialTicks) -> {
			GlStateManager.popAttrib();
		});
	}
	
	public static <T extends GuiComponent<?>> Option<T, Color> color(T component) {
		pushPopAttrib(component);
		if(!component.addTag(TAG_COLOR))
			return null;
		
		Option<T, Color> opt = new Option<>(Color.WHITE);
		component.preDraw.add((c, pos, partialTicks) -> {
			opt.getValue(component).glColor();
		});
		
		return opt;
	}
}
