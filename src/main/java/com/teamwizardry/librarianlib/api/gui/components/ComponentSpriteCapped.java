package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.client.DrawingUtil;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSpriteCapped extends GuiComponent<ComponentSpriteCapped> {
	
	Sprite topLeft, middle, bottomRight;
	boolean horizontal;
	
	public ComponentSpriteCapped(Sprite topLeft, Sprite middle, Sprite bottomRight, boolean horizontal, int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		
		this.topLeft = topLeft;
		this.middle = middle;
		this.bottomRight = bottomRight;
		this.horizontal = horizontal;
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		topLeft.getTex().bind();
		DrawingUtil.startDrawingSession();
		if(horizontal) {			
			topLeft.draw(getAnimationTicks(), pos.xf + 0, pos.yf + 0);
			bottomRight.draw(getAnimationTicks(), pos.xf + (float)( size.x-bottomRight.width ), pos.yf + 0);
			middle.drawClipped(getAnimationTicks(), pos.xf + topLeft.width, pos.yf + 0, (int)( size.x - (topLeft.width+bottomRight.width) ), middle.height);
		} else {			
			topLeft.draw(getAnimationTicks(), pos.xf + 0, pos.yf + 0);
			bottomRight.draw(getAnimationTicks(), pos.xf + 0, pos.yf + (float)( size.y-bottomRight.height ));
			middle.drawClipped(getAnimationTicks(), pos.xf + 0, pos.yf + topLeft.height, middle.height, (int)( size.y - (topLeft.height+bottomRight.height) ));
		}
		DrawingUtil.endDrawingSession();
	}

}
