package com.teamwizardry.librarianlib.features.saving.serializers.builtin.trove

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.safeCast
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
import kotlin.collections.MutableMap
import kotlin.collections.any
import kotlin.collections.count
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.mapOf
import kotlin.collections.set

@SerializerFactoryRegister
object SerializeTroveMapsFactory : SerializerFactory("TroveMaps") {

    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if(troveStuff.any {
            it.key.isAssignableFrom(type.clazz)
        }) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        @Suppress("UNCHECKED_CAST")
        val map = troveStuff.entries.find { it.key.isAssignableFrom(type.clazz) }!!.value as TroveMapData<Any>
        return SerializeTroveMap(type, map, getKeyType(type), getValueType(type))
    }

    private fun getKeyType(type: FieldType): FieldType {
        if(TMap::class.java.isAssignableFrom(type.clazz))
            return (type.genericSuperclass(TMap::class.java) as FieldTypeGeneric).generic(0)
        val setter = type.clazz.methods.filter { it.name == "set" }.first()
        val keyClass = setter.parameterTypes[0]

        return type.resolve(keyClass)
    }

    private fun getValueType(type: FieldType): FieldType {
        if(TMap::class.java.isAssignableFrom(type.clazz))
            return (type.genericSuperclass(TMap::class.java) as FieldTypeGeneric).generic(1)
        val setter = type.clazz.methods.filter { it.name == "set" }.first()
        val keyClass = setter.parameterTypes[1]

        return type.resolve(keyClass)
    }

    class SerializeTroveMap(type: FieldType, troveData: TroveMapData<Any>, keyType: FieldType, valueType: FieldType) : Serializer<Any>(type) {
        val serKey: Serializer<Any> by SerializerRegistry.lazy(keyType)
        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        val wrap: Any.() -> MutableMap<Any?, Any?> = troveData.wrapper

        override fun readNBT(nbt: NBTBase, existing: Any?, syncing: Boolean): Any {
            val list = nbt.safeCast<NBTTagList>()
            val map = constructor()
            val map_w = map.wrap()

            val existing_w = existing?.wrap()

            list.forEach<NBTTagCompound> {
                val keyTag = it.getTag("k")
                val valTag = it.getTag("v")
                val k = if (keyTag == null) null else serKey.read(keyTag, null, syncing)
                val v = if (valTag == null) null else serValue.read(valTag, existing_w?.get(k), syncing)
                map_w[k] = v
            }

            return map
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val value_w = value.wrap()
            for (k in value_w.keys) {
                val container = NBTTagCompound()
                list.appendTag(container)
                val v = value_w[k]

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
            val existing_w = existing?.wrap()
            val map = constructor()
            val map_w = map.wrap()

            val nullCount = buf.readVarInt()
            for (i in 0..nullCount - 1) {
                val k = serKey.read(buf, null, syncing)
                map_w[k] = null
            }

            val hasNullKey = buf.readBoolean()
            if (hasNullKey) {
                val isNullValue = buf.readBoolean()
                map_w[null] = if (isNullValue) null else serValue.read(buf, existing_w?.get(null), syncing)
            }

            val nonNullCount = buf.readVarInt()
            for (i in 0..nonNullCount - 1) {
                val k = serKey.read(buf, null, syncing)
                val v = serValue.read(buf, existing_w?.get(k), syncing)
                map_w[k] = v
            }

            return map
        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val value_w = value.wrap()
            val nulls = value_w.filter { it.value == null && it.key != null }
            buf.writeVarInt(nulls.count { it.key != null })
            nulls.forEach { serKey.write(buf, it.key!!, syncing) }

            buf.writeBoolean(value_w.containsKey(null))
            if (value_w.containsKey(null)) {
                buf.writeBoolean(value_w[null] == null)
                if (value_w[null] != null)
                    serValue.write(buf, value_w[null]!!, syncing)
            }

            val nonNulls = value_w.filter { it.value != null && it.key != null }
            buf.writeVarInt(nonNulls.size)
            nonNulls.forEach {
                serKey.write(buf, it.key!!, syncing)
                serValue.write(buf, it.value!!, syncing)
            }
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
    private val troveStuff = mapOf(
            TByteByteMap::class.java to TroveMapData<TByteByteMap>({ TByteByteHashMap() }, { TByteByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteCharMap::class.java to TroveMapData<TByteCharMap>({ TByteCharHashMap() }, { TByteCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteShortMap::class.java to TroveMapData<TByteShortMap>({ TByteShortHashMap() }, { TByteShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteIntMap::class.java to TroveMapData<TByteIntMap>({ TByteIntHashMap() }, { TByteIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteLongMap::class.java to TroveMapData<TByteLongMap>({ TByteLongHashMap() }, { TByteLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteFloatMap::class.java to TroveMapData<TByteFloatMap>({ TByteFloatHashMap() }, { TByteFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteDoubleMap::class.java to TroveMapData<TByteDoubleMap>({ TByteDoubleHashMap() }, { TByteDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TByteObjectMap::class.java to TroveMapData<TByteObjectMap<Any?>>({ TByteObjectHashMap() }, { TByteObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharByteMap::class.java to TroveMapData<TCharByteMap>({ TCharByteHashMap() }, { TCharByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharCharMap::class.java to TroveMapData<TCharCharMap>({ TCharCharHashMap() }, { TCharCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharShortMap::class.java to TroveMapData<TCharShortMap>({ TCharShortHashMap() }, { TCharShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharIntMap::class.java to TroveMapData<TCharIntMap>({ TCharIntHashMap() }, { TCharIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharLongMap::class.java to TroveMapData<TCharLongMap>({ TCharLongHashMap() }, { TCharLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharFloatMap::class.java to TroveMapData<TCharFloatMap>({ TCharFloatHashMap() }, { TCharFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharDoubleMap::class.java to TroveMapData<TCharDoubleMap>({ TCharDoubleHashMap() }, { TCharDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TCharObjectMap::class.java to TroveMapData<TCharObjectMap<Any?>>({ TCharObjectHashMap() }, { TCharObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortByteMap::class.java to TroveMapData<TShortByteMap>({ TShortByteHashMap() }, { TShortByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortCharMap::class.java to TroveMapData<TShortCharMap>({ TShortCharHashMap() }, { TShortCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortShortMap::class.java to TroveMapData<TShortShortMap>({ TShortShortHashMap() }, { TShortShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortIntMap::class.java to TroveMapData<TShortIntMap>({ TShortIntHashMap() }, { TShortIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortLongMap::class.java to TroveMapData<TShortLongMap>({ TShortLongHashMap() }, { TShortLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortFloatMap::class.java to TroveMapData<TShortFloatMap>({ TShortFloatHashMap() }, { TShortFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortDoubleMap::class.java to TroveMapData<TShortDoubleMap>({ TShortDoubleHashMap() }, { TShortDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TShortObjectMap::class.java to TroveMapData<TShortObjectMap<Any?>>({ TShortObjectHashMap() }, { TShortObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntByteMap::class.java to TroveMapData<TIntByteMap>({ TIntByteHashMap() }, { TIntByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntCharMap::class.java to TroveMapData<TIntCharMap>({ TIntCharHashMap() }, { TIntCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntShortMap::class.java to TroveMapData<TIntShortMap>({ TIntShortHashMap() }, { TIntShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntIntMap::class.java to TroveMapData<TIntIntMap>({ TIntIntHashMap() }, { TIntIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntLongMap::class.java to TroveMapData<TIntLongMap>({ TIntLongHashMap() }, { TIntLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntFloatMap::class.java to TroveMapData<TIntFloatMap>({ TIntFloatHashMap() }, { TIntFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntDoubleMap::class.java to TroveMapData<TIntDoubleMap>({ TIntDoubleHashMap() }, { TIntDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TIntObjectMap::class.java to TroveMapData<TIntObjectMap<Any?>>({ TIntObjectHashMap() }, { TIntObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongByteMap::class.java to TroveMapData<TLongByteMap>({ TLongByteHashMap() }, { TLongByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongCharMap::class.java to TroveMapData<TLongCharMap>({ TLongCharHashMap() }, { TLongCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongShortMap::class.java to TroveMapData<TLongShortMap>({ TLongShortHashMap() }, { TLongShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongIntMap::class.java to TroveMapData<TLongIntMap>({ TLongIntHashMap() }, { TLongIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongLongMap::class.java to TroveMapData<TLongLongMap>({ TLongLongHashMap() }, { TLongLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongFloatMap::class.java to TroveMapData<TLongFloatMap>({ TLongFloatHashMap() }, { TLongFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongDoubleMap::class.java to TroveMapData<TLongDoubleMap>({ TLongDoubleHashMap() }, { TLongDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TLongObjectMap::class.java to TroveMapData<TLongObjectMap<Any?>>({ TLongObjectHashMap() }, { TLongObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatByteMap::class.java to TroveMapData<TFloatByteMap>({ TFloatByteHashMap() }, { TFloatByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatCharMap::class.java to TroveMapData<TFloatCharMap>({ TFloatCharHashMap() }, { TFloatCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatShortMap::class.java to TroveMapData<TFloatShortMap>({ TFloatShortHashMap() }, { TFloatShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatIntMap::class.java to TroveMapData<TFloatIntMap>({ TFloatIntHashMap() }, { TFloatIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatLongMap::class.java to TroveMapData<TFloatLongMap>({ TFloatLongHashMap() }, { TFloatLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatFloatMap::class.java to TroveMapData<TFloatFloatMap>({ TFloatFloatHashMap() }, { TFloatFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatDoubleMap::class.java to TroveMapData<TFloatDoubleMap>({ TFloatDoubleHashMap() }, { TFloatDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TFloatObjectMap::class.java to TroveMapData<TFloatObjectMap<Any?>>({ TFloatObjectHashMap() }, { TFloatObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleByteMap::class.java to TroveMapData<TDoubleByteMap>({ TDoubleByteHashMap() }, { TDoubleByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleCharMap::class.java to TroveMapData<TDoubleCharMap>({ TDoubleCharHashMap() }, { TDoubleCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleShortMap::class.java to TroveMapData<TDoubleShortMap>({ TDoubleShortHashMap() }, { TDoubleShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleIntMap::class.java to TroveMapData<TDoubleIntMap>({ TDoubleIntHashMap() }, { TDoubleIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleLongMap::class.java to TroveMapData<TDoubleLongMap>({ TDoubleLongHashMap() }, { TDoubleLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleFloatMap::class.java to TroveMapData<TDoubleFloatMap>({ TDoubleFloatHashMap() }, { TDoubleFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleDoubleMap::class.java to TroveMapData<TDoubleDoubleMap>({ TDoubleDoubleHashMap() }, { TDoubleDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TDoubleObjectMap::class.java to TroveMapData<TDoubleObjectMap<Any?>>({ TDoubleObjectHashMap() }, { TDoubleObjectMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectByteMap::class.java to TroveMapData<TObjectByteMap<Any?>>({ TObjectByteHashMap() }, { TObjectByteMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectCharMap::class.java to TroveMapData<TObjectCharMap<Any?>>({ TObjectCharHashMap() }, { TObjectCharMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectShortMap::class.java to TroveMapData<TObjectShortMap<Any?>>({ TObjectShortHashMap() }, { TObjectShortMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectIntMap::class.java to TroveMapData<TObjectIntMap<Any?>>({ TObjectIntHashMap() }, { TObjectIntMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectLongMap::class.java to TroveMapData<TObjectLongMap<Any?>>({ TObjectLongHashMap() }, { TObjectLongMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectFloatMap::class.java to TroveMapData<TObjectFloatMap<Any?>>({ TObjectFloatHashMap() }, { TObjectFloatMapDecorator(this) as MutableMap<Any?, Any?> }),
            TObjectDoubleMap::class.java to TroveMapData<TObjectDoubleMap<Any?>>({ TObjectDoubleHashMap() }, { TObjectDoubleMapDecorator(this) as MutableMap<Any?, Any?> }),
            TMap::class.java to TroveMapData<TMap<Any?, Any?>>({ THashMap() }, { this })
    )
}

class TroveMapData<T>(val constructor: () -> T, val wrapper: T.() -> MutableMap<Any?, Any?>)
