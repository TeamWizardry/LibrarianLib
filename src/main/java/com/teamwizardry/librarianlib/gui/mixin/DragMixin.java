package com.teamwizardry.librarianlib.gui.mixin;

import com.teamwizardry.librarianlib.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.HandlerList;
import com.teamwizardry.librarianlib.math.Vec2d;

import java.util.function.Function;

public class DragMixin<T extends GuiComponent<?>> {
	
	public final HandlerList<IDragCancelableEvent<T>> pickup = new HandlerList<>();
	public final HandlerList<IDragCancelableEvent<T>> drop = new HandlerList<>();
	public final HandlerList<IDragEvent<T>> drag = new HandlerList<>();
	public boolean mouseDown = false;
	public Vec2d clickPos = Vec2d.ZERO;
	protected T component;
	protected Function<Vec2d, Vec2d> constraints;
	
	public DragMixin(T component, Function<Vec2d, Vec2d> constraints) {
		this.component = component;
		this.constraints = constraints;
		init();
	}
	
	private void init() {
		component.mouseDown.add( (c, pos, button) -> {
			if(!mouseDown && c.isMouseOver(pos) && !pickup.fireCancel((h) -> h.handle(component, button, pos))) {
				mouseDown = true;
				clickPos = pos;
				return true;
			}
			return false;
		});
		component.mouseUp.add( (c, pos, button) -> {
			if(mouseDown && !drop.fireCancel((h) -> h.handle(component, button, pos)))
				mouseDown = false;
			return false;
		});
		component.preDraw.add( (c, pos, partialTicks) -> {
			if(mouseDown) {
				Vec2d newPos = constraints.apply(  c.getPos().add(pos).sub(clickPos)  );
				
				if(!newPos.equals(c.getPos())) {
					c.setPos(newPos);
					drag.fireAll((h) -> h.handle(component, newPos));
				}
			}
		});
	}
	
	@FunctionalInterface
	public interface IDragEvent<T> {
		void handle(T component, Vec2d pos);
	}
	
	@FunctionalInterface
	public interface IDragCancelableEvent<T> {
		boolean handle(T component, EnumMouseButton button, Vec2d pos);
	}
}
