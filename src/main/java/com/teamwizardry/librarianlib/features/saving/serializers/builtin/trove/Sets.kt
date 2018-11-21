package com.teamwizardry.librarianlib.features.saving.serializers.builtin.trove

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.writeVarInt
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
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
        return if (troveStuff.any { it.key.isAssignableFrom(type.clazz) })
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
        return type.resolveGeneric(troveStuff.keys.find { it.isAssignableFrom(type.clazz) }!!, 0)
    }

    class SerializeTroveSet(type: FieldType, setData: TroveSetData<Any>, valueType: FieldType) : Serializer<Any>(type) {
        override fun getDefault(): Any {
            return constructor()
        }

        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        @Suppress("UNCHECKED_CAST")
        val wrap: Any.() -> MutableSet<Any?> = setData.wrapper as Any.() -> MutableSet<Any?>

        override fun readNBT(nbt: NBTBase, existing: Any?, syncing: Boolean): Any {
            val compound = nbt.castOrDefault(NBTTagCompound::class.java)
            val list = compound.getTag("values").castOrDefault(NBTTagList::class.java)
            val nullFlag = compound.getBoolean("hasNull")

            val set = existing ?: constructor()
            val wrappedSet = set.wrap()
            wrappedSet.clear()
            if (nullFlag)
                wrappedSet.add(null)
            list.forEach<NBTTagCompound> {
                val v = serValue.read(it, null, syncing)
                wrappedSet.add(v)
            }

            return set
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val wrappedValue = value.wrap()

            wrappedValue
                    .filterNotNull()
                    .forEach { list.appendTag(serValue.write(it, syncing)) }

            val compound = NBTTagCompound()
            compound.setBoolean("hasNull", wrappedValue.contains(null))
            compound.setTag("values", list)
            return compound
        }

        override fun readBytes(buf: ByteBuf, existing: Any?, syncing: Boolean): Any {
            val nullFlag = buf.readBoolean()
            val len = buf.readVarInt() - if (nullFlag) 1 else 0

            val set = existing ?: constructor()
            val wrappedSet = set.wrap()
            wrappedSet.clear()
            if (nullFlag)
                wrappedSet.add(null)

            for (i in 0 until len) {
                wrappedSet.add(serValue.read(buf, null, syncing))
            }

            return set
        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val wrappedValue = value.wrap()
            buf.writeBoolean(wrappedValue.contains(null))
            buf.writeVarInt(wrappedValue.size)

            wrappedValue
                    .filterNotNull()
                    .forEach { serValue.write(buf, it, syncing) }
        }

        private fun createConstructorMethodHandle(): () -> Any {
            val constructor = troveStuff[type.clazz]?.constructor
            if (constructor != null)
                return constructor

            val mh = MethodHandleHelper.wrapperForConstructor<Any>(type.clazz)
            return { mh(arrayOf()) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val troveStuff = mapOf(
            TByteSet::class.java to TroveSetData(::TByteHashSet, ::TByteSetDecorator),
            TCharSet::class.java to TroveSetData(::TCharHashSet, ::TCharSetDecorator),
            TShortSet::class.java to TroveSetData(::TShortHashSet, ::TShortSetDecorator),
            TIntSet::class.java to TroveSetData(::TIntHashSet, ::TIntSetDecorator),
            TLongSet::class.java to TroveSetData(::TLongHashSet, ::TLongSetDecorator),
            TFloatSet::class.java to TroveSetData(::TFloatHashSet, ::TFloatSetDecorator),
            TDoubleSet::class.java to TroveSetData(::TDoubleHashSet, ::TDoubleSetDecorator)
    )
}

class TroveSetData<T>(val constructor: () -> T, val wrapper: T.() -> MutableSet<out Any?>)
