package com.teamwizardry.librarianlib.facade.testmod.containers.base

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.SimpleSerializer
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

abstract class TestContainerData: INBTSerializable<CompoundNBT> {
    protected val serializer: SimpleSerializer<Any> = SimpleSerializer.get(this.javaClass)

    override fun serializeNBT(): CompoundNBT {
        return serializer.createTag(this, Save::class.java)
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        serializer.applyTag(nbt, this, Save::class.java)
    }

    open fun tick() {}
}
