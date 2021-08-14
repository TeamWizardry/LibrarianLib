package com.teamwizardry.librarianlib.facade.test.controllers.base

import com.teamwizardry.librarianlib.scribe.Save
import com.teamwizardry.librarianlib.scribe.SimpleSerializer
import com.teamwizardry.librarianlib.scribe.Sync
import net.minecraft.nbt.NbtCompound

abstract class TestControllerData {
    protected val serializer: SimpleSerializer<Any> = SimpleSerializer.get(this.javaClass)

    open fun serialize(): NbtCompound {
        return serializer.createTag(this, Save::class.java)
    }

    open fun deserialize(nbt: NbtCompound) {
        serializer.applyTag(nbt, this, Save::class.java)
    }

    open fun serializeSync(): NbtCompound {
        return serializer.createTag(this, Sync::class.java)
    }

    open fun deserializeSync(nbt: NbtCompound) {
        serializer.applyTag(nbt, this, Sync::class.java)
    }

    open fun tick() {}
}
