package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.INBT
import net.minecraftforge.common.util.INBTSerializable

class INBTSerializableSerializer: NBTSerializer<INBTSerializable<INBT>>() {
    override fun deserialize(tag: INBT, existing: INBTSerializable<INBT>?): INBTSerializable<INBT> {
        if(existing == null)
            throw DeserializationException("INBTSerializable requires an existing value to deserialize")
        existing.deserializeNBT(tag)
        return existing
    }

    override fun serialize(value: INBTSerializable<INBT>): INBT {
        return value.serializeNBT()
    }
}