package com.teamwizardry.librarianlib.core

/**
 * Created by Elad on 1/27/2017.
 */
import com.teamwizardry.librarianlib.common.util.builders.json
import org.spongepowered.asm.mixin.Mixins

fun getJsonStringForConfig(paakage: String, mixins: List<String>, client: List<String>, server: List<String>): String {
    return json { obj("package" to paakage, "mixins" to mixins, "client" to client, "server" to server) }.toString()
}

fun registerPathForMixinsConfig(vararg paths: String) {
    Mixins.addConfigurations(*paths)
}

/**
 * You cannot use lambdas, method references, constructor references, or anonymous
 * classes in Mixins! Use these methods or create your own in other, non-Mixin
 * classes.
 */
fun <T> createSupplier(value: T) = { value }

fun <T, E> createSupplier(@Suppress("UNUSED_PARAMETER") key: E, value: T): (E) -> T = { e: E -> value }
