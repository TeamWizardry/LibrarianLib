package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.trove

import com.teamwizardry.librarianlib.common.util.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readVarInt
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.writeVarInt
import gnu.trove.decorator.*
import gnu.trove.set.*
import gnu.trove.set.hash.*
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeTroveSetsFactory : SerializerFactory("TroveSets") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if(troveStuff.any { it.key.isAssignableFrom(type.clazz) })
            SerializerFactoryMatch.GENERAL
        else
            SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        val set = troveStuff.entries.find { it.key.isAssignableFrom(type.clazz) }!!
        @Suppress("UNCHECKED_CAST")
        return SerializeTroveSet(type, set.value as TroveSetData<Any>, getKeyType(type))
    }

    private fun getKeyType(type: FieldType): FieldType {
        val supertype = type.genericSuperclass(troveStuff.keys.find { it.isAssignableFrom(type.clazz) }!!) as FieldTypeGeneric
        return supertype.generic(0)
    }

    class SerializeTroveSet(type: FieldType, setData: TroveSetData<Any>, valueType: FieldType) : Serializer<Any>(type) {

        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        val wrap: Any.() -> MutableSet<Any?> = setData.wrapper

        override fun readNBT(nbt: NBTBase, existing: Any?, syncing: Boolean): Any {
            val compound = nbt.safeCast(NBTTagCompound::class.java)
            val list = compound.getTag("values").safeCast(NBTTagList::class.java)
            val nullFlag = compound.getBoolean("hasNull")

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            val set_w = set.wrap()
            set_w.clear()
            if (nullFlag)
                set_w.add(null)
            list.forEachIndexed<NBTTagCompound> { i, tag ->
                val v = serValue.read(tag, null, syncing)
                set_w.add(v)
            }

            return set
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val value_w = value.wrap()

            value_w
                    .filterNotNull()
                    .forEach { list.appendTag(serValue.write(it, syncing)) }

            val compound = NBTTagCompound()
            compound.setBoolean("hasNull", value_w.contains(null))
            compound.setTag("values", list)
            return compound
        }

        override fun readBytes(buf: ByteBuf, existing: Any?, syncing: Boolean): Any {
            val nullFlag = buf.readBoolean()
            val len = buf.readVarInt() - if (nullFlag) 1 else 0

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            val set_w = set.wrap()
            set_w.clear()
            if (nullFlag)
                set_w.add(null)

            for (i in 0..len - 1) {
                set_w.add(serValue.read(buf, null, syncing))
            }

            return set
        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val value_w = value.wrap()
            buf.writeBoolean(value_w.contains(null))
            buf.writeVarInt(value_w.size)

            value_w
                    .filterNotNull()
                    .forEach { serValue.write(buf, it, syncing) }
        }

        private fun createConstructorMethodHandle(): () -> Any {
            val constructor = troveStuff.get(type.clazz)?.constructor
            if(constructor != null)
                return constructor

            val mh = MethodHandleHelper.wrapperForConstructor<Any>(type.clazz)
            return { mh(arrayOf()) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val troveStuff = mapOf(
            TByteSet::class.java to TroveSetData<TByteSet>({ TByteHashSet() }, { TByteSetDecorator(this) as MutableSet<Any?> }),
            TCharSet::class.java to TroveSetData<TCharSet>({ TCharHashSet() }, { TCharSetDecorator(this) as MutableSet<Any?> }),
            TShortSet::class.java to TroveSetData<TShortSet>({ TShortHashSet() }, { TShortSetDecorator(this) as MutableSet<Any?> }),
            TIntSet::class.java to TroveSetData<TIntSet>({ TIntHashSet() }, { TIntSetDecorator(this) as MutableSet<Any?> }),
            TLongSet::class.java to TroveSetData<TLongSet>({ TLongHashSet() }, { TLongSetDecorator(this) as MutableSet<Any?> }),
            TFloatSet::class.java to TroveSetData<TFloatSet>({ TFloatHashSet() }, { TFloatSetDecorator(this) as MutableSet<Any?> }),
            TDoubleSet::class.java to TroveSetData<TDoubleSet>({ TDoubleHashSet() }, { TDoubleSetDecorator(this) as MutableSet<Any?> })
    )
}

class TroveSetData<T>(val constructor: () -> T, val wrapper: T.() -> MutableSet<Any?>)
