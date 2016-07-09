package com.teamwizardry.librarianlib.api.gui.components.mixin;

import com.teamwizardry.librarianlib.api.LibrarianLog;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;

public class ButtonMixin {
	public static final String TAG = "HasButtonMixin";
	
	
	private IStateChanger normal, disabled, hover;
	private IClickHandler handler;
	
	public final GuiComponent<?> component;
	public EnumButtonState state = EnumButtonState.NORMAL;
	
	
	public ButtonMixin(GuiComponent<?> component,
			IStateChanger normal, IStateChanger hover, IStateChanger disabled, IClickHandler clickHandler) {
		this.component = component;
		this.normal = normal;
		this.hover = hover;
		this.disabled = disabled;
		this.handler = clickHandler;
		
		if(!component.hasTag(TAG))
			apply();
		else
			LibrarianLog.I.warn("Component already has button mixin!");
	}
	
	private void apply() {
		
		component.addTag(TAG);
		
		component.preDraw.add((c, pos, partialTicks) -> {
			EnumButtonState newState = state;
			if(!c.isEnabled())
				newState = EnumButtonState.DISABLED;
			else if(c.mouseOverThisFrame)
				newState = EnumButtonState.HOVER;
			else
				newState = EnumButtonState.NORMAL;
			if(newState != state) {
				state = newState;
				switch (state) {
				case NORMAL:
					normal.changeState();
					break;
				case HOVER:
					hover.changeState();
					break;
				case DISABLED:
					disabled.changeState();
					break;
				default:
					break;
				}
			}
		});
		
		component.mouseClick.add((c, pos, button) -> {
			handler.handle();
			return true;
		});
		
		normal.changeState();
	}
	
	public static enum EnumButtonState {
		NORMAL, DISABLED, HOVER
	}
	
	@FunctionalInterface
	public static interface IStateChanger {
		void changeState();
	}
	
	@FunctionalInterface
	public static interface IClickHandler {
		void handle();
	}
}
