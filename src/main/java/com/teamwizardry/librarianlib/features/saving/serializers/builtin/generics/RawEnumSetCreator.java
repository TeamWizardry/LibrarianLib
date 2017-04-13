package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics;

import java.util.EnumSet;

public class RawEnumSetCreator {
	@SuppressWarnings("unchecked")
	public static EnumSet create(Class clazz) {
		return EnumSet.noneOf(clazz);
	}
}
