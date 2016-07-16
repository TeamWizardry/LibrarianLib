package com.teamwizardry.librarianlib.api.gui.components.mixin;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.math.Vec2;

public class ScissorMixin {

	public static void scissor(GuiComponent<?> component) {
		component.preDraw.add( (c, pos, partialTicks) -> {
			Vec2 root = c.rootPos(new Vec2(0,0));
			ScissorUtil.push();
			ScissorUtil.set(root.xi, root.yi, c.getSize().xi, c.getSize().yi);
	        ScissorUtil.enable();
		});
		component.postDraw.addFirst( (c, pos, partialTicks) -> {
			ScissorUtil.pop();
		});
	}
	
}
