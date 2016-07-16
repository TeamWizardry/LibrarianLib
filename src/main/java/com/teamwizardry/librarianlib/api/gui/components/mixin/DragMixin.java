package com.teamwizardry.librarianlib.api.gui.components.mixin;

import java.util.function.Function;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2;

public class DragMixin {
	
	protected GuiComponent<?> component;
	protected Function<Vec2, Vec2> constraints;
	
	protected boolean mouseDown = false;
	protected Vec2 clickPos = Vec2.ZERO;
	
	public DragMixin(GuiComponent<?> component, Function<Vec2, Vec2> constraints) {
		this.component = component;
		this.constraints = constraints;
		init();
	}
	
	private void init() {
		component.mouseDown.add( (c, pos, button) -> {
			if(c.isMouseOver(pos)) {
				mouseDown = true;
				clickPos = pos;
				return true;
			}
			return false;
		});
		component.mouseUp.add( (c, pos, button) -> {
			mouseDown = false;
			return false;
		});
		component.preDraw.add( (c, pos, partialTicks) -> {
			if(mouseDown)
				c.setPos(
					constraints.apply(  c.getPos().add(pos).sub(clickPos)  )
				);
		});
	}
}
