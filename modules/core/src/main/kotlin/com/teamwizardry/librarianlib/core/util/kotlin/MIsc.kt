package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

fun String.toRl(): ResourceLocation = ResourceLocation(this)

/**
 * Creates a translation key in the format `type.namespace.path[.suffix]`, e.g. `item.minecraft.iron_ingot`
 */
fun ResourceLocation.translationKey(type: String, suffix: String? = null): String
    = "$type.$namespace.$path${suffix?.let { ".$it" } ?: ""}"

/**
 * True if the current environment is obfuscated
 */
val IS_DEOBFUSCATED: Boolean = Minecraft::currentScreen.name == "currentScreen"

fun obf(deobfuscated: String, obfuscated: String): String = if(IS_DEOBFUSCATED) deobfuscated else obfuscated

fun unreachable(): Nothing {
    throw UnreachableException()
}

class UnreachableException: RuntimeException()
