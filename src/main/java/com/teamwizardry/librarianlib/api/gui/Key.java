package com.teamwizardry.librarianlib.api.gui;

import java.util.HashMap;
import java.util.Map;

public class Key {

	public final char character;
	public final int keyCode;
	
	private Key(char character, int keyCode) {
		this.character = character;
		this.keyCode = keyCode;
	}
	
	/**
	 * Maps a keycode to a key
	 */
	private static Map<Integer, Key> pool = new HashMap<>();
	
	public static Key get(char character, int keyCode) {
		if(!pool.containsKey(keyCode))
			pool.put(keyCode, new Key(character, keyCode));
		return pool.get(keyCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + keyCode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (keyCode != other.keyCode)
			return false;
		return true;
	}
	
}
