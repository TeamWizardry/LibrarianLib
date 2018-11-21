package com.teamwizardry.librarianlib.features.saving.serializers.builtin.trove

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.writeVarInt
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import gnu.trove.decorator.*
import gnu.trove.map.*
import gnu.trove.map.hash.*
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import kotlin.collections.set

@SerializerFactoryRegister
object SerializeTroveMapsFactory : SerializerFactory("TroveMaps") {

    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if (troveStuff.any {
            it.key.isAssignableFrom(type.clazz)
        }) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        @Suppress("UNCHECKED_CAST")
        val map = troveStuff.entries.find { it.key.isAssignableFrom(type.clazz) }!!.value as TroveMapData<Any>
        return SerializeTroveMap(type, map, getKeyType(type), getValueType(type))
    }

    private fun getKeyType(type: FieldType): FieldType {
        if (TMap::class.java.isAssignableFrom(type.clazz))
            return (type.genericSuperclass(TMap::class.java) as FieldTypeGeneric).generic(0)
        val setter = type.clazz.methods.first { it.name == "put" }
        val keyClass = setter.parameterTypes[0]
        val keyAnnot = setter.annotatedParameterTypes[0]

        return type.resolve(keyClass, keyAnnot)
    }

    private fun getValueType(type: FieldType): FieldType {
        if (TMap::class.java.isAssignableFrom(type.clazz))
            return (type.genericSuperclass(TMap::class.java) as FieldTypeGeneric).generic(1)
        val setter = type.clazz.methods.first { it.name == "put" }
        val keyClass = setter.parameterTypes[1]
        val keyAnnot = setter.annotatedParameterTypes[1]

        return type.resolve(keyClass, keyAnnot)
    }

    class SerializeTroveMap(type: FieldType, troveData: TroveMapData<Any>, keyType: FieldType, valueType: FieldType) : Serializer<Any>(type) {
        override fun getDefault(): Any {
            return constructor()
        }

        val serKey: Serializer<Any> by SerializerRegistry.lazy(keyType)
        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        @Suppress("UNCHECKED_CAST")
        val wrap: Any.() -> MutableMap<Any?, Any?> = troveData.wrapper as Any.() -> MutableMap<Any?, Any?>

        @Suppress("UNCHECKED_CAST")
        override fun readNBT(nbt: NBTBase, existing: Any?, syncing: Boolean): Any {
            val list = nbt.castOrDefault(NBTTagList::class.java)
            val map = constructor()
            val mapW = map.wrap()

            val existingW = existing?.wrap()

            list.forEach<NBTTagCompound> {
                val keyTag = it.getTag("k")
                val valTag = it.getTag("v")
                val k = serKey.read(keyTag, null, syncing)
                val v = serValue.read(valTag, existingW?.get(k), syncing)
                mapW[k] = v
            }

            return map
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val valueW = value.wrap()
            for (k in valueW.keys) {
                val container = NBTTagCompound()
                list.appendTag(container)
                val v = valueW[k]

                if (k != null) {
                    container.setTag("k", serKey.write(k, syncing))
                }
                if (v != null) {
                    container.setTag("v", serValue.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: Any?, syncing: Boolean): Any {
            val existingW = existing?.wrap()
            val map = constructor()
            val mapW = map.wrap()

            val nullCount = buf.readVarInt()
            for (i in 0 until nullCount) {
                val k = serKey.read(buf, null, syncing)
                mapW[k] = null
            }

            val hasNullKey = buf.readBoolean()
            if (hasNullKey) {
                val isNullValue = buf.readBoolean()
                mapW[null] = if (isNullValue) null else serValue.read(buf, existingW?.get(null), syncing)
            }

            val nonNullCount = buf.readVarInt()
            for (i in 0 until nonNullCount) {
                val k = serKey.read(buf, null, syncing)
                val v = serValue.read(buf, existingW?.get(k), syncing)
                mapW[k] = v
            }

            return map
        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val wrapped = value.wrap()
            val nulls = wrapped.filter { it.value == null && it.key != null }
            buf.writeVarInt(nulls.count { it.key != null })
            nulls.forEach { serKey.write(buf, it.key!!, syncing) }

            buf.writeBoolean(wrapped.containsKey(null))
            if (wrapped.containsKey(null)) {
                buf.writeBoolean(wrapped[null] == null)
                if (wrapped[null] != null)
                    serValue.write(buf, wrapped[null]!!, syncing)
            }

            val nonNulls = wrapped.filter { it.value != null && it.key != null }
            buf.writeVarInt(nonNulls.size)
            nonNulls.forEach {
                serKey.write(buf, it.key!!, syncing)
                serValue.write(buf, it.value!!, syncing)
            }
        }

        private fun createConstructorMethodHandle(): () -> Any {
            val constructor = troveStuff[type.clazz]?.constructor
            if (constructor != null)
                return constructor
            val mh = MethodHandleHelper.wrapperForConstructor<Any>(type.clazz)
            return { mh(arrayOf()) }
        }
    }

    private val troveStuff = mapOf(
            TByteByteMap::class.java to TroveMapData(::TByteByteHashMap, ::TByteByteMapDecorator),
            TByteCharMap::class.java to TroveMapData(::TByteCharHashMap, ::TByteCharMapDecorator),
            TByteShortMap::class.java to TroveMapData(::TByteShortHashMap, ::TByteShortMapDecorator),
            TByteIntMap::class.java to TroveMapData(::TByteIntHashMap, ::TByteIntMapDecorator),
            TByteLongMap::class.java to TroveMapData(::TByteLongHashMap, ::TByteLongMapDecorator),
            TByteFloatMap::class.java to TroveMapData(::TByteFloatHashMap, ::TByteFloatMapDecorator),
            TByteDoubleMap::class.java to TroveMapData(::TByteDoubleHashMap, ::TByteDoubleMapDecorator),
            TByteObjectMap::class.java to TroveMapData<TByteObjectMap<Any?>>(::TByteObjectHashMap, ::TByteObjectMapDecorator),
            TCharByteMap::class.java to TroveMapData(::TCharByteHashMap, ::TCharByteMapDecorator),
            TCharCharMap::class.java to TroveMapData(::TCharCharHashMap, ::TCharCharMapDecorator),
            TCharShortMap::class.java to TroveMapData(::TCharShortHashMap, ::TCharShortMapDecorator),
            TCharIntMap::class.java to TroveMapData(::TCharIntHashMap, ::TCharIntMapDecorator),
            TCharLongMap::class.java to TroveMapData(::TCharLongHashMap, ::TCharLongMapDecorator),
            TCharFloatMap::class.java to TroveMapData(::TCharFloatHashMap, ::TCharFloatMapDecorator),
            TCharDoubleMap::class.java to TroveMapData(::TCharDoubleHashMap, ::TCharDoubleMapDecorator),
            TCharObjectMap::class.java to TroveMapData<TCharObjectMap<Any?>>(::TCharObjectHashMap, ::TCharObjectMapDecorator),
            TShortByteMap::class.java to TroveMapData(::TShortByteHashMap, ::TShortByteMapDecorator),
            TShortCharMap::class.java to TroveMapData(::TShortCharHashMap, ::TShortCharMapDecorator),
            TShortShortMap::class.java to TroveMapData(::TShortShortHashMap, ::TShortShortMapDecorator),
            TShortIntMap::class.java to TroveMapData(::TShortIntHashMap, ::TShortIntMapDecorator),
            TShortLongMap::class.java to TroveMapData(::TShortLongHashMap, ::TShortLongMapDecorator),
            TShortFloatMap::class.java to TroveMapData(::TShortFloatHashMap, ::TShortFloatMapDecorator),
            TShortDoubleMap::class.java to TroveMapData(::TShortDoubleHashMap, ::TShortDoubleMapDecorator),
            TShortObjectMap::class.java to TroveMapData<TShortObjectMap<Any?>>(::TShortObjectHashMap, ::TShortObjectMapDecorator),
            TIntByteMap::class.java to TroveMapData(::TIntByteHashMap, ::TIntByteMapDecorator),
            TIntCharMap::class.java to TroveMapData(::TIntCharHashMap, ::TIntCharMapDecorator),
            TIntShortMap::class.java to TroveMapData(::TIntShortHashMap, ::TIntShortMapDecorator),
            TIntIntMap::class.java to TroveMapData(::TIntIntHashMap, ::TIntIntMapDecorator),
            TIntLongMap::class.java to TroveMapData(::TIntLongHashMap, ::TIntLongMapDecorator),
            TIntFloatMap::class.java to TroveMapData(::TIntFloatHashMap, ::TIntFloatMapDecorator),
            TIntDoubleMap::class.java to TroveMapData(::TIntDoubleHashMap, ::TIntDoubleMapDecorator),
            TIntObjectMap::class.java to TroveMapData<TIntObjectMap<Any?>>(::TIntObjectHashMap, ::TIntObjectMapDecorator),
            TLongByteMap::class.java to TroveMapData(::TLongByteHashMap, ::TLongByteMapDecorator),
            TLongCharMap::class.java to TroveMapData(::TLongCharHashMap, ::TLongCharMapDecorator),
            TLongShortMap::class.java to TroveMapData(::TLongShortHashMap, ::TLongShortMapDecorator),
            TLongIntMap::class.java to TroveMapData(::TLongIntHashMap, ::TLongIntMapDecorator),
            TLongLongMap::class.java to TroveMapData(::TLongLongHashMap, ::TLongLongMapDecorator),
            TLongFloatMap::class.java to TroveMapData(::TLongFloatHashMap, ::TLongFloatMapDecorator),
            TLongDoubleMap::class.java to TroveMapData(::TLongDoubleHashMap, ::TLongDoubleMapDecorator),
            TLongObjectMap::class.java to TroveMapData<TLongObjectMap<Any?>>(::TLongObjectHashMap, ::TLongObjectMapDecorator),
            TFloatByteMap::class.java to TroveMapData(::TFloatByteHashMap, ::TFloatByteMapDecorator),
            TFloatCharMap::class.java to TroveMapData(::TFloatCharHashMap, ::TFloatCharMapDecorator),
            TFloatShortMap::class.java to TroveMapData(::TFloatShortHashMap, ::TFloatShortMapDecorator),
            TFloatIntMap::class.java to TroveMapData(::TFloatIntHashMap, ::TFloatIntMapDecorator),
            TFloatLongMap::class.java to TroveMapData(::TFloatLongHashMap, ::TFloatLongMapDecorator),
            TFloatFloatMap::class.java to TroveMapData(::TFloatFloatHashMap, ::TFloatFloatMapDecorator),
            TFloatDoubleMap::class.java to TroveMapData(::TFloatDoubleHashMap, ::TFloatDoubleMapDecorator),
            TFloatObjectMap::class.java to TroveMapData<TFloatObjectMap<Any?>>(::TFloatObjectHashMap, ::TFloatObjectMapDecorator),
            TDoubleByteMap::class.java to TroveMapData(::TDoubleByteHashMap, ::TDoubleByteMapDecorator),
            TDoubleCharMap::class.java to TroveMapData(::TDoubleCharHashMap, ::TDoubleCharMapDecorator),
            TDoubleShortMap::class.java to TroveMapData(::TDoubleShortHashMap, ::TDoubleShortMapDecorator),
            TDoubleIntMap::class.java to TroveMapData(::TDoubleIntHashMap, ::TDoubleIntMapDecorator),
            TDoubleLongMap::class.java to TroveMapData(::TDoubleLongHashMap, ::TDoubleLongMapDecorator),
            TDoubleFloatMap::class.java to TroveMapData(::TDoubleFloatHashMap, ::TDoubleFloatMapDecorator),
            TDoubleDoubleMap::class.java to TroveMapData(::TDoubleDoubleHashMap, ::TDoubleDoubleMapDecorator),
            TDoubleObjectMap::class.java to TroveMapData<TDoubleObjectMap<Any?>>(::TDoubleObjectHashMap, ::TDoubleObjectMapDecorator),
            TObjectByteMap::class.java to TroveMapData<TObjectByteMap<Any?>>(::TObjectByteHashMap, ::TObjectByteMapDecorator),
            TObjectCharMap::class.java to TroveMapData<TObjectCharMap<Any?>>(::TObjectCharHashMap, ::TObjectCharMapDecorator),
            TObjectShortMap::class.java to TroveMapData<TObjectShortMap<Any?>>(::TObjectShortHashMap, ::TObjectShortMapDecorator),
            TObjectIntMap::class.java to TroveMapData<TObjectIntMap<Any?>>(::TObjectIntHashMap, ::TObjectIntMapDecorator),
            TObjectLongMap::class.java to TroveMapData<TObjectLongMap<Any?>>(::TObjectLongHashMap, ::TObjectLongMapDecorator),
            TObjectFloatMap::class.java to TroveMapData<TObjectFloatMap<Any?>>(::TObjectFloatHashMap, ::TObjectFloatMapDecorator),
            TObjectDoubleMap::class.java to TroveMapData<TObjectDoubleMap<Any?>>(::TObjectDoubleHashMap, ::TObjectDoubleMapDecorator),
            TMap::class.java to TroveMapData<TMap<Any?, Any?>>(::THashMap) { this }
    )
}

class TroveMapData<T>(val constructor: () -> T, val wrapper: T.() -> MutableMap<out Any?, out Any?>)
