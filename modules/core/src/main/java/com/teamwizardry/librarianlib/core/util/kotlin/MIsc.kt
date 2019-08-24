package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

fun String.toRl(): ResourceLocation = ResourceLocation(this)

val IS_DEOBFUSCATED: Boolean = Minecraft::class.simpleName == "Minecraft"

fun obf(deobfuscated: String, obfuscated: String): String = if(IS_DEOBFUSCATED) deobfuscated else obfuscated

fun unreachable(): Nothing {
    throw UnreachableException()
}

class UnreachableException: RuntimeException()
