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

/**
 * Used for cases where code is unreachable, but the language demands a value be returned. For the case where the
 * condition should never fail, but theoretically could, use [inconceivable].
 *
 * ```kotlin
 * enum class SomeEnum {
 *     A, B, C, D
 * }
 *
 * // if you know the parameter will only ever be A or B
 * fun someFunction(SomeEnum value): String  {
 *     return when(value) {
 *         SomeEnum.A -> "it was A"
 *         SomeEnum.B -> "it was B"
 *         // we need a "default value" for the impossible
 *         // case where the value isn't A or B
 *         else -> unreachable()
 *     }
 * }
 * ```
 */
fun unreachable(): Nothing {
    throw UnreachableException()
}

class UnreachableException: RuntimeException()

/**
 * Used when a condition should never fail, but theoretically could. For the case where the code is literally impossible
 * but the compiler demands a return or throw, use [unreachable]
 *
 * > "You keep using that word. I do not think it means what you think it means." — Inigo Montoya
 */
fun inconceivable(message: String): Nothing {
    throw InconceivableException(message)
}

class InconceivableException(message: String): RuntimeException(message)
