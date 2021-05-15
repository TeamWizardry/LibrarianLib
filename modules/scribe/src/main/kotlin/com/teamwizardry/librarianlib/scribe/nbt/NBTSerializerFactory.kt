package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.SerializerFactory

public abstract class NBTSerializerFactory(
    prism: NbtPrism, pattern: TypeMirror,
    predicates: (TypeMirror) -> Boolean = { true }
): SerializerFactory<NbtSerializer<*>>(prism, pattern, predicates)
