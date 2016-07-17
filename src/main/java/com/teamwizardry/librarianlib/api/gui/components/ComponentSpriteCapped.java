package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
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
			topLeft.draw(pos.xf + 0, pos.yf + 0);
			bottomRight.draw(pos.xf + (float)( size.x-bottomRight.getWidth() ), pos.yf + 0);
			middle.drawClipped(pos.xf + topLeft.getWidth(), pos.yf + 0, (int)( size.x - (topLeft.getWidth()+bottomRight.getWidth()) ), middle.getHeight());
		} else {			
			topLeft.draw(pos.xf + 0, pos.yf + 0);
			bottomRight.draw(pos.xf + 0, pos.yf + (float)( size.y-bottomRight.getHeight() ));
			middle.drawClipped(pos.xf + 0, pos.yf + topLeft.getHeight(), middle.getWidth(), (int)( size.y - (topLeft.getHeight()+bottomRight.getHeight()) ));
		}
		DrawingUtil.endDrawingSession();
	}

}
