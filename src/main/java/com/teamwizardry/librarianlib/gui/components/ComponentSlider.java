package com.teamwizardry.librarianlib.gui.components;

import java.util.function.Consumer;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.HandlerList;
import com.teamwizardry.librarianlib.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.math.MathUtil;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ComponentSlider extends GuiComponent<ComponentSlider> {

	public HandlerList<Consumer<Double>> percentageChange = new HandlerList<>();
	
	public final ComponentVoid handle;
	private double percentage;
	public int increments;
	private Vec2d handlePos;
	
	public ComponentSlider(int posX, int posY, int width, int height, double percentage, int increments) {
		super(posX, posY, width, height);
		this.increments = increments;
		
		handle = new ComponentVoid(0, 0);
		setPercentage(percentage);
		
		new DragMixin<ComponentVoid>(handle, (vec) -> {
			vec = vec.projectOnTo(size);
			vec = Vec2d.max(vec, Vec2d.ZERO); // clamp to the begining
			vec = Vec2d.min(vec, size); // clamp to the end
			
			setPercentage(vec.length()/size.length());
			
			return vec;
		});
		
		handle.preDraw.add((c, pos, partialTicks) -> {
			if(!handlePos.equals(handle.getPos()))
				handle.setPos(handlePos);
		});
		
		add(handle);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		// noop
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		percentage = MathUtil.clamp(percentage, 0, 1);
		if(increments > 0)
			percentage = MathUtil.round(percentage, 1.0/increments);
		handlePos = size.mul(percentage);
		this.percentage = percentage;
		percentageChange.fireAll((h) -> h.accept(this.percentage));
	}
}
