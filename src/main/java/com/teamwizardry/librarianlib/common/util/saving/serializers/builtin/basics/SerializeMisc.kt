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
        ({ nbt, existing, sync ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Color(tag.getByte("r").toInt() and 0xFF, tag.getByte("g").toInt() and 0xFF, tag.getByte("b").toInt() and 0xFF, tag.getByte("a").toInt() and 0xFF)
        }, { value, sync ->
            val tag = NBTTagCompound()
            tag.setByte("r", value.red.toByte())
            tag.setByte("g", value.green.toByte())
            tag.setByte("b", value.blue.toByte())
            tag.setByte("a", value.alpha.toByte())
            tag
        }))

        SerializerRegistry["awt:color"]?.register(Targets.BYTES, Targets.BYTES.impl<Color>
        ({ buf, existing, sync ->
            Color(buf.readUnsignedByte().toInt(), buf.readUnsignedByte().toInt(), buf.readUnsignedByte().toInt(), buf.readUnsignedByte().toInt())
        }, { buf, value, sync ->
            buf.writeByte(value.red)
            buf.writeByte(value.green)
            buf.writeByte(value.blue)
            buf.writeByte(value.alpha)
        }))
    }

    private fun nbtTagCompound() {
        SerializerRegistry.register("minecraft:nbttagcompound", Serializer(NBTTagCompound::class.java))

        SerializerRegistry["minecraft:nbttagcompound"]?.register(Targets.NBT, Targets.NBT.impl<NBTTagCompound>
        ({ nbt, existing, sync ->
            nbt.safeCast(NBTTagCompound::class.java)
        }, { value, sync ->
            value
        }))

        SerializerRegistry["minecraft:nbttagcompound"]?.register(Targets.BYTES, { buf, existing, sync ->
            buf.readTag()
        }, { buf, value, sync ->
            buf.writeTag(value as NBTTagCompound)
        })
    }

    private fun itemStack() {
        SerializerRegistry.register("minecraft:itemstack", Serializer(ItemStack::class.java))

        SerializerRegistry["minecraft:itemstack"]?.register(Targets.NBT, Targets.NBT.impl<ItemStack>
        ({ nbt, existing, sync ->
            ItemStack.loadItemStackFromNBT(nbt.safeCast(NBTTagCompound::class.java))
        }, { value, sync ->
            value.writeToNBT(NBTTagCompound())
        }))

        SerializerRegistry["minecraft:itemstack"]?.register(Targets.BYTES, Targets.BYTES.impl<ItemStack>
        ({ buf, existing, sync ->
            buf.readStack()
        }, { buf, value, sync ->
            buf.writeStack(value)
        }))
    }

    private fun itemStackHandler() {
        SerializerRegistry.register("forge:itemstackhandler", Serializer(ItemStackHandler::class.java))

        SerializerRegistry["forge:itemstackhandler"]?.register(Targets.NBT, Targets.NBT.impl<ItemStackHandler>
        ({ nbt, existing, sync ->
            val handler = ItemStackHandler()
            handler.deserializeNBT(nbt.safeCast(NBTTagCompound::class.java))
            handler
        }, { value, sync ->
            value.serializeNBT()
        }))

        SerializerRegistry["forge:itemstackhandler"]?.register(Targets.BYTES, Targets.BYTES.impl<ItemStackHandler>
        ({ buf, existing, sync ->
            val handler = existing ?: ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        }, { buf, value, sync ->
            buf.writeTag(value.serializeNBT())
        }))
    }
}
