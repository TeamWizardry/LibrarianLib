package com.teamwizardry.librarianlib.gui.template;

import com.teamwizardry.librarianlib.gui.GuiComponent;

public class ComponentTemplate<T extends GuiComponent<?>> {

	protected T result;
	
	public T get() {
		return result;
	}
	
}
