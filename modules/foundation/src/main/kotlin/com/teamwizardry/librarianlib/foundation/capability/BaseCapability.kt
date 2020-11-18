package com.teamwizardry.librarianlib.foundation.capability

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.SimpleSerializer
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

/**
 * A base class for capability implementations. Annotate fields with [@Save][Save] to have them serialized.
 */
public abstract class BaseCapability: INBTSerializable<CompoundNBT> {
    protected val serializer: SimpleSerializer<Any> = SimpleSerializer.get(this.javaClass)

    override fun serializeNBT(): CompoundNBT {
        return serializer.createTag(this, Save::class.java)
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        serializer.applyTag(nbt, this, Save::class.java)
    }
}