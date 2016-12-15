package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
object SerializeMisc {
    init {
        color()
        nbtTagCompound()
        itemStack()
        itemStackHandler()
    }

    private fun color() {
        SerializerRegistry.register("awt:color", Serializer(Color::class.java))

        SerializerRegistry["awt:color"]?.register(Targets.NBT, Targets.NBT.impl<Color>
        ({ nbt, existing ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Color(tag.getByte("r").toInt(), tag.getByte("g").toInt(), tag.getByte("b").toInt(), tag.getByte("a").toInt())
        }, { value ->
            val tag = NBTTagCompound()
            tag.setByte("r", value.red.toByte())
            tag.setByte("g", value.green.toByte())
            tag.setByte("b", value.blue.toByte())
            tag.setByte("a", value.alpha.toByte())
            tag
        }))

        SerializerRegistry["awt:color"]?.register(Targets.BYTES, Targets.BYTES.impl<Color>
        ({ buf, existing ->
            Color(buf.readByte().toInt(), buf.readByte().toInt(), buf.readByte().toInt(), buf.readByte().toInt())
        }, { buf, value ->
            buf.writeByte(value.red)
            buf.writeByte(value.green)
            buf.writeByte(value.blue)
            buf.writeByte(value.alpha)
        }))
    }

    private fun nbtTagCompound() {
        SerializerRegistry.register("minecraft:nbttagcompound", Serializer(NBTTagCompound::class.java))

        SerializerRegistry["minecraft:nbttagcompound"]?.register(Targets.NBT, Targets.NBT.impl<NBTTagCompound>
        ({ nbt, existing ->
            nbt.safeCast(NBTTagCompound::class.java)
        }, { value ->
            value
        }))

        SerializerRegistry["minecraft:nbttagcompound"]?.register(Targets.BYTES, { buf, existing ->
            buf.readTag()
        }, { buf, value ->
            buf.writeTag(value as NBTTagCompound)
        })
    }

    private fun itemStack() {
        SerializerRegistry.register("minecraft:itemstack", Serializer(ItemStack::class.java))

        SerializerRegistry["minecraft:itemstack"]?.register(Targets.NBT, Targets.NBT.impl<ItemStack>
        ({ nbt, existing ->
            ItemStack.loadItemStackFromNBT(nbt.safeCast(NBTTagCompound::class.java))
        }, { value ->
            value.writeToNBT(NBTTagCompound())
        }))

        SerializerRegistry["minecraft:itemstack"]?.register(Targets.BYTES, Targets.BYTES.impl<ItemStack>
        ({ buf, existing ->
            buf.readStack()
        }, { buf, value ->
            buf.writeStack(value)
        }))
    }

    private fun itemStackHandler() {
        SerializerRegistry.register("forge:itemstackhandler", Serializer(ItemStackHandler::class.java))

        SerializerRegistry["forge:itemstackhandler"]?.register(Targets.NBT, Targets.NBT.impl<ItemStackHandler>
        ({ nbt, existing ->
            val handler = ItemStackHandler()
            handler.deserializeNBT(nbt.safeCast(NBTTagCompound::class.java))
            handler
        }, { value ->
            value as ItemStackHandler
            value.serializeNBT()
        }))

        SerializerRegistry["forge:itemstackhandler"]?.register(Targets.BYTES, Targets.BYTES.impl<ItemStackHandler>
        ({ buf, existing ->
            val handler = existing as ItemStackHandler? ?: ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        }, { buf, value ->
            value as ItemStackHandler
            buf.writeTag(value.serializeNBT())
        }))
    }
}
