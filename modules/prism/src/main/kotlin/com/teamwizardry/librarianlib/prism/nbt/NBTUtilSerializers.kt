package com.teamwizardry.librarianlib.prism.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.prism.nbt.StringSerializer.expectType
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.nbt.StringNBT
import net.minecraft.util.math.BlockPos
import java.util.UUID

object BlockStateSerializer: NBTSerializer<BlockState>() {
    override fun deserialize(tag: INBT, existing: BlockState?): BlockState {
        return NBTUtil.readBlockState(tag.expectType("tag"))
    }

    override fun serialize(value: BlockState): INBT {
        return NBTUtil.writeBlockState(value)
    }
}

object BlockPosSerializer: NBTSerializer<BlockPos>() {
    override fun deserialize(tag: INBT, existing: BlockPos?): BlockPos {
        return NBTUtil.readBlockPos(tag.expectType("tag"))
    }

    override fun serialize(value: BlockPos): INBT {
        return NBTUtil.writeBlockPos(value)
    }
}

object UUIDSerializer: NBTSerializer<UUID>() {
    override fun deserialize(tag: INBT, existing: UUID?): UUID {
        return NBTUtil.readUniqueId(tag.expectType("tag"))
    }

    override fun serialize(value: UUID): INBT {
        return NBTUtil.writeUniqueId(value)
    }
}

object GameProfileSerializer: NBTSerializer<GameProfile>() {
    override fun deserialize(tag: INBT, existing: GameProfile?): GameProfile {
        return NBTUtil.readGameProfile(tag.expectType("tag"))
            ?: throw DeserializationException("Reading GameProfile") // it only returns null if an error occurs
    }

    override fun serialize(value: GameProfile): INBT {
        val tag = CompoundNBT()
        NBTUtil.writeGameProfile(tag, value)
        return tag
    }
}
