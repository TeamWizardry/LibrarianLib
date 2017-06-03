package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics;

import java.util.EnumSet;

/**
 * Created by TheCodeWarrior because kotlin won't let me do this kinda shit
 */
public class RawEnumSetCreator {
    @SuppressWarnings("unchecked")
    public static EnumSet create(Class clazz) {
        return EnumSet.noneOf((Class<Enum>) clazz);
    }
}
