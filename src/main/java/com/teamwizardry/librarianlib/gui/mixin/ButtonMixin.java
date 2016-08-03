package com.teamwizardry.librarianlib.gui.mixin;

import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.librarianlib.gui.GuiComponent;

public class ButtonMixin {
	public static final String TAG = "HasButtonMixin";
	public final GuiComponent<?> component;
	public EnumButtonState state = EnumButtonState.NORMAL;
	private IStateChanger normal, disabled, hover;
	private IClickHandler handler;
	
	
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
			if(state != EnumButtonState.DISABLED)
				handler.handle();
			return state != EnumButtonState.DISABLED;
		});
		
		normal.changeState();
	}
	
	public enum EnumButtonState {
		NORMAL, DISABLED, HOVER
	}
	
	@FunctionalInterface
	public interface IStateChanger {
		void changeState();
	}
	
	@FunctionalInterface
	public interface IClickHandler {
		void handle();
	}
}
