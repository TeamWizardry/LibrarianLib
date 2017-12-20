package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Created by TheCodeWarrior because kotlin won't let me do this kinda shit
 */
@SuppressWarnings("unchecked")
public class RawTypeConstructors {
    public static EnumSet createEnumSet(Class clazz) {
        return EnumSet.noneOf((Class<Enum>) clazz);
    }

    public static EnumMap createEnumMap(Class clazz) {
        return new EnumMap(clazz);
    }
}
