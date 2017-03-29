package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics;

import java.util.EnumSet;

/**
 * Created by TheCodeWarrior
 */
public class RawEnumSetCreator {
    public static EnumSet create(Class clazz) {
        return EnumSet.noneOf(clazz);
    }
}
