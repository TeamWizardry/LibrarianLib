package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.util.ResourceLocation

fun String.toRl(): ResourceLocation = ResourceLocation(this)

fun unreachable(): Nothing {
    throw UnreachableException()
}

class UnreachableException: RuntimeException()
