package com.teamwizardry.librarianlib.core.util

import net.minecraftforge.fml.common.Mod

public object MiscUtil {
    public fun getModId(clazz: Class<*>): String {
        val modAnnotation = clazz.declaredAnnotations.filterIsInstance<Mod>().firstOrNull()
            ?: throw IllegalStateException("Could not find mod annotation on ${clazz.canonicalName}")
        return modAnnotation.value
    }
}