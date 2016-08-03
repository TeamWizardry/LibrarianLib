package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.HandlerList;
import com.teamwizardry.librarianlib.gui.mixin.ScissorMixin;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentScrolledView extends GuiComponent<ComponentScrolledView> {

	public final HandlerList<IScrollEvent<ComponentScrolledView>> scroll = new HandlerList<>();
	
	protected Vec2d offset = Vec2d.ZERO;
	
	public ComponentScrolledView(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		ScissorMixin.scissor(this);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		// noop
	}
	
	@Override
	public Vec2d transformChildPos(GuiComponent<?> child, Vec2d pos) {
		return super.transformChildPos(child, pos).add(offset);
	}
	
	public void scrollTo(Vec2d scroll) {
		scroll = Vec2d.min(getMaxScroll(), scroll);
		if(!scroll.equals(offset)) {
			this.scroll.fireModifier(scroll, (h, v) -> h.handle(this, offset, v));
			offset = scroll;
		}
	}
	
	public void scrollOffset(Vec2d scroll) {
		scrollTo(offset.add(scroll));
	}
	
	public void scrollToPercent(Vec2d scroll) {
		scrollTo(getMaxScroll().mul(scroll));
	}
	
	@Override
	public BoundingBox2D getLogicalSize() {
		return getContentSize();
	}
	
	@Override
	public void draw(Vec2d mousePos, float partialTicks) {
		GlStateManager.translate(-offset.x, -offset.y, 0);
		super.draw(mousePos, partialTicks);
		GlStateManager.translate(offset.x, offset.y, 0);
	}
	
	public Vec2d getMaxScroll() {
		return super.getLogicalSize().max.sub(pos).sub(size);
	}

	@FunctionalInterface
	public interface IScrollEvent<T> {
		Vec2d handle(T component, Vec2d oldScroll, Vec2d newScroll);
	}
	
}
