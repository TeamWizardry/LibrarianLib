package com.teamwizardry.librarianlib.api.gui;

import java.util.ArrayList;
import java.util.List;

public class HandlerList<T> {

	private List<T> handlers = new ArrayList<>();
	
	public void add(T handler) {
		handlers.add(handler);
	}
	
	public void fire(IHandlerCaller<T> caller) {
		for (T t : handlers) {
			caller.call(t);
		}
	}
	
	@FunctionalInterface
	public static interface IHandlerCaller<T> {
		public void call(T handler);
	}
}
