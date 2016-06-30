package com.teamwizardry.librarianlib.api.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of handlers for an event
 *
 * @param <T> The handler type
 */
public class HandlerList<T> {

	private List<T> handlers = new ArrayList<>();
	
	/**
	 * Add a handler to this event
	 */
	public void add(T handler) {
		handlers.add(handler);
	}
	
	/**
	 * Fire an event, each handler will be passed to the caller 
	 * @param caller
	 */
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
