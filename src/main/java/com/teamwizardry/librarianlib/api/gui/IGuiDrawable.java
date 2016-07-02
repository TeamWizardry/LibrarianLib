package com.teamwizardry.librarianlib.api.gui;

import com.teamwizardry.librarianlib.math.Vec2;

/**
 * An object that can be drawn to a bookcomponents
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
