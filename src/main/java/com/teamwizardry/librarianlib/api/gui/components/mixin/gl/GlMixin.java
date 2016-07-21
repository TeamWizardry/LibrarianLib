package com.teamwizardry.librarianlib.api.gui.components.mixin.gl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.misc.Color;

public class GlMixin {
	
	public static final String
		TAG_ATTRIB = "mixin_attrib",
		TAG_MATRIX = "mixin_matrix",
		TAG_COLOR  = "mixin_color",
		TAG_TRANSFORM = "mixin_transform";
	
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
	
	public static void pushPopMatrix(GuiComponent<?> component) {
		if(!component.addTag(TAG_MATRIX))
			return;
		
		component.preDraw.add((c, pos, partialTicks) -> {
			GlStateManager.pushMatrix();
		});
		component.postDraw.addFirst((c, pos, partialTicks) -> {
			GlStateManager.popMatrix();
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
	
	public static <T extends GuiComponent<?>> Option<T, Vec3d> transform(T component) {
		pushPopMatrix(component);
		if(!component.addTag(TAG_TRANSFORM))
			return null;
		
		Option<T, Vec3d> opt = new Option<>(Vec3d.ZERO);
		component.preDraw.add((c, pos, partialTicks) -> {
			Vec3d v = opt.getValue(component);
			GlStateManager.translate(v.xCoord, v.yCoord, v.zCoord);
		});
		
		return opt;
	}
}
