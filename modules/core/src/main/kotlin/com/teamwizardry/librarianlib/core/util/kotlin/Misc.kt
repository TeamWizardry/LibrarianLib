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
 *     A, B, C;
 *
 *     val isC: Boolean get() = this == SomeEnum.C
 * }
 *
 * fun someFunction(SomeEnum value): String  {
 *     if(value.isC) {
 *         return "it was C"
 *     }
 *     // at this point `value` will never be C, but the compiler doesn't know that
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
 * In the immortal words of Inigo Montoya, "You keep using that word. I do not think it means what you think it means."
 */
fun inconceivable(message: String): Nothing {
    throw InconceivableException(message)
}

class InconceivableException(message: String): RuntimeException(message)

/**
 * A more boring name for [inconceivable], here simply for autocomplete and searching. "Impossible" is also ambiguous in
 * that some things _should_ be impossible, while others are _literally_ impossible. "Impossible" makes no distinction.
 */
@Deprecated("Don't be boring and ambiguous, use `unreachable` or `inconceivable`")
fun impossible(message: String): Nothing {
    inconceivable(message)
}