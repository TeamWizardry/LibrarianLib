package com.teamwizardry.librarianlib.gui;

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
	 * Add a handler to this event at the begining of the list
	 */
	public void addFirst(T handler) {
		handlers.add(0, handler);
	}
	
	/**
	 * Fire an event, each handler will be passed to the caller
	 * @param caller
	 */
	public void fireAll(IHandlerCaller<T> caller) {
		for (T t : handlers) {
			caller.call(t);
		}
	}
	
	/**
	 * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
	 * @param caller
	 */
	public boolean fireCancel(ICancelableHandlerCaller<T> caller) {
		for (T t : handlers) {
			if(caller.call(t))
				return true;
		}
		return false;
	}
	
	/**
	 * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
	 * @param caller
	 */
	public <V> V fireModifier(V value, IModifierHandlerCaller<V, T> caller) {
		for (T t : handlers) {
			value = caller.call(t, value);
		}
		return value;
	}
	
	@FunctionalInterface
	public interface IHandlerCaller<T> {
		void call(T handler);
	}
	
	@FunctionalInterface
	public interface ICancelableHandlerCaller<T> {
		boolean call(T handler);
	}
	
	@FunctionalInterface
	public interface IModifierHandlerCaller<V, T> {
		V call(T handler, V value);
	}
}
