package com.teamwizardry.librarianlib.scribe

@Target(AnnotationTarget.FIELD)
@SimpleSerializationMarker
public annotation class Save(val value: String = "")

@Target(AnnotationTarget.FIELD)
@SimpleSerializationMarker
public annotation class Sync(val value: String = "")
