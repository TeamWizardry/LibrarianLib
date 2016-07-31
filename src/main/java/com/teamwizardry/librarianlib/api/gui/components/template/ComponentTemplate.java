package com.teamwizardry.librarianlib.api.gui.components.template;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;

public class ComponentTemplate<T extends GuiComponent<?>> {

	protected T result;
	
	public T get() {
		return result;
	}
	
}
