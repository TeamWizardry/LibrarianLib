package com.teamwizardry.librarianlib.api.gui.components.input;

import java.util.function.Consumer;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.api.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.api.gui.components.mixin.DragMixin;
import com.teamwizardry.librarianlib.math.MathUtil;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSlider extends GuiComponent<ComponentSlider> {

	public HandlerList<Consumer<Double>> percentageChange = new HandlerList<>();
	
	public final ComponentVoid handle;
	private double percentage;
	public int increments;
	private Vec2 handlePos;
	
	/**
	 * 
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 * @param percentage
	 * @param increments number of snap points excluding the ends
	 */
	public ComponentSlider(int posX, int posY, int width, int height, double percentage, int increments) {
		super(posX, posY, width, height);
		this.increments = increments;
		
		handle = new ComponentVoid(0, 0);
		setPercentage(percentage);
		
		new DragMixin<ComponentVoid>(handle, (vec) -> {
			vec = vec.projectOnTo(size);
			vec = Vec2.max(vec, Vec2.ZERO); // clamp to the begining
			vec = Vec2.min(vec, size); // clamp to the end
			
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
	public void drawComponent(Vec2 mousePos, float partialTicks) {
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
