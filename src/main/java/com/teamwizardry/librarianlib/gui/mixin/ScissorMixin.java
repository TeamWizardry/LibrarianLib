package com.teamwizardry.librarianlib.gui.mixin;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.util.ScissorUtil;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ScissorMixin {

	public static void scissor(GuiComponent<?> component) {
		component.preDraw.add( (c, pos, partialTicks) -> {
			Vec2d root = c.rootPos(new Vec2d(0,0));
			ScissorUtil.push();
			ScissorUtil.set(root.xi, root.yi, c.getSize().xi, c.getSize().yi);
	        ScissorUtil.enable();
		});
		component.postDraw.addFirst( (c, pos, partialTicks) -> {
			ScissorUtil.pop();
		});
	}
	
}
