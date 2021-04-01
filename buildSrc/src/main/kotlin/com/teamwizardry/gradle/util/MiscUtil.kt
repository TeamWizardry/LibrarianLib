package com.teamwizardry.gradle.util

import org.gradle.api.reflect.TypeOf

inline fun <reified T> typeOf(): TypeOf<T> {
    return object : TypeOf<T>() {}
}