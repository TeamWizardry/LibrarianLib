package com.teamwizardry.libarianlib.client.gui;

import com.teamwizardry.libarianlib.math.Vec2;

/**
 * An object that can be drawn to a gui
 * @author Pierce Corcoran
 */
public interface IGuiDrawable {

	/**
	 * Draw this object to the screen.
	 * @param mousePos
	 * @param partialTicks
	 */
	void draw(Vec2 mousePos, float partialTicks);
	
}
