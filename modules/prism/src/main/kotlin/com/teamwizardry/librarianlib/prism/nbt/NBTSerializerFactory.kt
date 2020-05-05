package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.SerializerFactory

abstract class NBTSerializerFactory(
    prism: NBTPrism, pattern: TypeMirror,
    predicates: (TypeMirror) -> Boolean = { true }
): SerializerFactory<NBTSerializer<*>>(prism, pattern, predicates)
