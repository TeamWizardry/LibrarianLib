package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.kotlin.writeVarInt
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeSetFactory : SerializerFactory("Set") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplySubclass(type, Set::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeSet(type, type.resolveGeneric(Set::class.java, 0))
    }

    class SerializeSet(type: FieldType, valueType: FieldType) : Serializer<MutableSet<Any?>>(type) {
        override fun getDefault(): MutableSet<Any?> {
            return mutableSetOf()
        }

        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        override fun readNBT(nbt: NBTBase, existing: MutableSet<Any?>?, syncing: Boolean): MutableSet<Any?> {
            val compound = nbt.safeCast(NBTTagCompound::class.java)
            val list = compound.getTag("values").safeCast(NBTTagList::class.java)
            val nullFlag = compound.getBoolean("hasNull")

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            set.clear()
            if (nullFlag)
                set.add(null)
            list.forEach<NBTBase> {
                val v = serValue.read(it, null, syncing)
                set.add(v)
            }

            return set
        }

        override fun writeNBT(value: MutableSet<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            value
                    .filterNotNull()
                    .forEach { list.appendTag(serValue.write(it, syncing)) }

            val compound = NBTTagCompound()
            compound.setBoolean("hasNull", value.contains(null))
            compound.setTag("values", list)
            return compound
        }

        override fun readBytes(buf: ByteBuf, existing: MutableSet<Any?>?, syncing: Boolean): MutableSet<Any?> {
            val nullFlag = buf.readBoolean()
            val len = buf.readVarInt() - if (nullFlag) 1 else 0

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            set.clear()
            if (nullFlag)
                set.add(null)

            for (i in 0..len - 1) {
                set.add(serValue.read(buf, null, syncing))
            }

            return set
        }

        override fun writeBytes(buf: ByteBuf, value: MutableSet<Any?>, syncing: Boolean) {
            buf.writeBoolean(value.contains(null))
            buf.writeVarInt(value.size)

            value
                    .filterNotNull()
                    .forEach { serValue.write(buf, it, syncing) }
        }

        private fun createConstructorMethodHandle(): () -> MutableSet<Any?> {

                    if(type.clazz == Set::class.java) {
                        return { LinkedHashSet<Any?>() } // linked so if order is important it's preserved.
                    } else if (EnumSet::class.java.isAssignableFrom(type.clazz)) {
                        @Suppress("UNCHECKED_CAST")
                        return { RawEnumSetCreator.create(type.clazz) as MutableSet<Any?> }
                    } else {
                        val mh = MethodHandleHelper.wrapperForConstructor<MutableSet<Any?>>(type.clazz)
                        return { mh(arrayOf()) }
                    }
        }
    }
}
