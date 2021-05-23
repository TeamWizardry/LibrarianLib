package com.teamwizardry.librarianlib.facade.test.controllers.base

import com.teamwizardry.librarianlib.scribe.Save
import com.teamwizardry.librarianlib.scribe.SimpleSerializer
import com.teamwizardry.librarianlib.scribe.Sync
import net.minecraft.nbt.CompoundTag

abstract class TestControllerData {
    protected val serializer: SimpleSerializer<Any> = SimpleSerializer.get(this.javaClass)

    open fun serialize(): CompoundTag {
        return serializer.createTag(this, Save::class.java)
    }

    open fun deserialize(nbt: CompoundTag) {
        serializer.applyTag(nbt, this, Save::class.java)
    }

    open fun serializeSync(): CompoundTag {
        return serializer.createTag(this, Sync::class.java)
    }

    open fun deserializeSync(nbt: CompoundTag) {
        serializer.applyTag(nbt, this, Sync::class.java)
    }

    open fun tick() {}
}
