package com.teamwizardry.librarianlib.util;

import java.util.HashMap;

public class DefaultedMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 5615718886973854633L;
	
	protected V defaultValue;

	public DefaultedMap(V defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public V get(Object k) {
		return this.getOrDefault(k, defaultValue);
	}
}
