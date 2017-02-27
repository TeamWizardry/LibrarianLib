package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.saving.helpers.SavableItemStackHandler
import com.teamwizardry.librarianlib.common.util.saving.helpers.StaticSavableItemStackHandler
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagByteArray
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.util.*


/**
 * Created by TheCodeWarrior
 */
object SerializeMisc {
    init {
        color()
        nbtTagCompound()
        itemStack()
        itemStackHandler()
        uuid()
        iTextComponent()
        resourcelocation()
        bytebuf()
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
            Color(buf.readInt())
        }, { buf, value, sync ->
            buf.writeInt(value.rgb)
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
            ItemStack(nbt.safeCast(NBTTagCompound::class.java))
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
        SerializerRegistry.register("forge:itemstackhandler", Serializer(ItemStackHandler::class.java, SavableItemStackHandler::class.java, StaticSavableItemStackHandler::class.java))

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

    private fun uuid() {
        SerializerRegistry.register("java:uuid", Serializer(UUID::class.java))

        SerializerRegistry["java:uuid"]?.register(Targets.NBT, Targets.NBT.impl<UUID>
        ({ nbt, existing, sync ->
            val tag = nbt as? NBTTagString
            if (tag == null)
                UUID.randomUUID()
            else
                UUID.fromString(tag.string)
        }, { value, sync ->
            NBTTagString(value.toString())
        }))

        SerializerRegistry["java:uuid"]?.register(Targets.BYTES, Targets.BYTES.impl<UUID>
        ({ buf, existing, sync ->
            UUID(buf.readLong(), buf.readLong())
        }, { buf, value, sync ->
            buf.writeLong(value.mostSignificantBits)
            buf.writeLong(value.leastSignificantBits)
        }))
    }

    private fun iTextComponent() {
        SerializerRegistry.register("minecraft:itextcomponent", Serializer(ITextComponent::class.java))

        SerializerRegistry["minecraft:itextcomponent"]?.register(Targets.NBT, Targets.NBT.impl<ITextComponent>({ nbt, existing, sync ->
            ITextComponent.Serializer.jsonToComponent(nbt.safeCast<NBTTagString>().string)
        }, { value, sync ->
            NBTTagString(ITextComponent.Serializer.componentToJson(value))
        }))

        SerializerRegistry["minecraft:itextcomponent"]?.register(Targets.BYTES, Targets.BYTES.impl<ITextComponent>({ buf, existing, sync ->
            PacketBuffer(buf).readTextComponent()
        }, { buf, value, sync ->
            PacketBuffer(buf).writeTextComponent(value)
        }))
    }

    private fun resourcelocation() {
        SerializerRegistry.register("minecraft:resourcelocation", Serializer(ResourceLocation::class.java))

        SerializerRegistry["minecraft:resourcelocation"]?.register(Targets.NBT, Targets.NBT.impl<ResourceLocation>
        ({ nbt, existing, sync ->
            val tag = nbt as? NBTTagString
            if (tag == null)
                ResourceLocation("minecraft:missingno")
            else
                ResourceLocation(tag.string)
        }, { value, sync ->
            NBTTagString(value.toString())
        }))

        SerializerRegistry["minecraft:resourcelocation"]?.register(Targets.BYTES, Targets.BYTES.impl<ResourceLocation>
        ({ buf, existing, sync ->
            ResourceLocation(buf.readString())
        }, { buf, value, sync ->
            buf.writeString(value.toString())
        }))
    }

    private fun bytebuf() {
        SerializerRegistry.register("netty:bytebuf", Serializer(ByteBuf::class.java))

        SerializerRegistry["netty:bytebuf"]?.register(Targets.NBT, Targets.NBT.impl<ByteBuf>
        ({ nbt, existing, sync ->
            val tag = nbt as? NBTTagByteArray
            if (tag == null)
                Unpooled.EMPTY_BUFFER
            else {
                Unpooled.wrappedBuffer(tag.byteArray)
            }
        }, { value, sync ->
            val bytes = ByteArray(value.readableBytes())
            value.readBytes(bytes)
            NBTTagByteArray(bytes)
        }))

        SerializerRegistry["netty:bytebuf"]?.register(Targets.BYTES, Targets.BYTES.impl<ByteBuf>
        ({ buf, existing, sync ->
            val bytes = ByteArray(buf.readVarInt())
            buf.readBytes(bytes)
            Unpooled.wrappedBuffer(bytes)
        }, { buf, value, sync ->
            val bytes = ByteArray(value.readableBytes())
            value.readBytes(bytes)
            buf.writeVarInt(bytes.size)
            buf.writeBytes(bytes)
        }))
    }
}
