@file:JvmName("NBTPrism")
package com.teamwizardry.librarianlib.prism

import com.teamwizardry.librarianlib.prism.nbt.BooleanSerializer
import com.teamwizardry.librarianlib.prism.nbt.ByteSerializer
import com.teamwizardry.librarianlib.prism.nbt.CharSerializer
import com.teamwizardry.librarianlib.prism.nbt.DoubleSerializer
import com.teamwizardry.librarianlib.prism.nbt.FloatSerializer
import com.teamwizardry.librarianlib.prism.nbt.IntSerializer
import com.teamwizardry.librarianlib.prism.nbt.LongSerializer
import com.teamwizardry.librarianlib.prism.nbt.NBTPrism
import com.teamwizardry.librarianlib.prism.nbt.NBTSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveBooleanSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveByteSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveCharSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveDoubleSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveFloatSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveIntSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveLongSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveShortSerializer
import com.teamwizardry.librarianlib.prism.nbt.ShortSerializer
import com.teamwizardry.librarianlib.prism.nbt.StringSerializer
import dev.thecodewarrior.prism.Prism

@get:JvmSynthetic
inline val NBTPrism: NBTPrism get() = instance

val instance: NBTPrism = Prism<NBTSerializer<*>>().also { prism ->
    prism.register(
        StringSerializer,
        LongSerializer,
        IntSerializer,
        ShortSerializer,
        ByteSerializer,
        CharSerializer,
        DoubleSerializer,
        FloatSerializer,
        BooleanSerializer,
        PrimitiveLongSerializer,
        PrimitiveIntSerializer,
        PrimitiveShortSerializer,
        PrimitiveByteSerializer,
        PrimitiveCharSerializer,
        PrimitiveDoubleSerializer,
        PrimitiveFloatSerializer,
        PrimitiveBooleanSerializer
    )
}