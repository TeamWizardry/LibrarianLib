package com.teamwizardry.librarianlib.scribe

import kotlin.reflect.KClass

object AutoCodec {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Record()

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.BINARY)
    annotation class Field(val name: String)

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class Register(val type: KClass<*>, val array: Boolean = false)
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ScribeMetadata(val data: String)
