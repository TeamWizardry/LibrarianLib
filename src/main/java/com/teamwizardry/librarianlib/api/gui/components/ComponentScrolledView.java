package com.teamwizardry.librarianlib.api.gui.components;

import net.minecraft.client.renderer.GlStateManager;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.api.gui.components.mixin.ScissorMixin;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentScrolledView extends GuiComponent<ComponentScrolledView> {

	public final HandlerList<IScrollEvent<ComponentScrolledView>> scroll = new HandlerList<>();
	
	protected Vec2 offset = Vec2.ZERO;
	
	public ComponentScrolledView(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		ScissorMixin.scissor(this);
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		// noop
	}
	
	public void scrollTo(Vec2 scroll) {
		scroll = Vec2.min(offset, scroll);
		if(!scroll.equals(offset)) {
			this.scroll.fireModifier(scroll, (h, v) -> h.handle(this, offset, v));
			offset = scroll;
		}
	}
	
	public void scrollOffset(Vec2 scroll) {
		scrollTo(offset.add(scroll));
	}
	
	@Override
	public BoundingBox2D getLogicalSize() {
		return getContentSize();
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		GlStateManager.translate(-offset.x, -offset.y, 0);
		super.draw(mousePos, partialTicks);
		GlStateManager.translate(offset.x, offset.y, 0);
	}
	
	public Vec2 getMaxScroll() {
		return super.getLogicalSize().max.sub(pos).sub(size);
	}

	@FunctionalInterface
	public static interface IScrollEvent<T> {
		Vec2 handle(T component, Vec2 oldScroll, Vec2 newScroll);
	}
	
}
