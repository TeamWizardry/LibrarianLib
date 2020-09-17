package com.teamwizardry.librarianlib.foundation.tileentity

import com.teamwizardry.librarianlib.prism.SimpleSerializationMarker

@Target(AnnotationTarget.FIELD)
@SimpleSerializationMarker
public annotation class Save

@Target(AnnotationTarget.FIELD)
@SimpleSerializationMarker
public annotation class Sync
