package com.teamwizardry.librarianlib.api.gui;

import java.util.function.Function;

/**
 * An option that can be defined by setting a value or by a callback
 * @author Pierce Corcoran
 *
 * @param <T> The type returned
 * @param <P> The type of the parameter to the option
 */
public class Option<P, T> {

	protected T value;
	protected T defaultValue;
	protected Function<P, T> callback;
	
	public Option(T defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public T getValue(P param) {
		if(callback == null)
			return value == null ? defaultValue : value;
		return callback.apply(param);
	}
	
	public void func(Function<P, T> callback) {
		this.callback = callback;
	}
	
	public void noFunc() {
		this.callback = null;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
}
