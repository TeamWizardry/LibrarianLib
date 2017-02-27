package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.trove.map

import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readVarInt
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeClass
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerPriority
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeVarInt
import gnu.trove.map.TLongFloatMap
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongFloatHashMap
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.relauncher.ReflectionHelper

/**
 * Created by TheCodeWarrior
 */
object SerializeTroveMaps {

    init {

        /*
        abstractMap(T_K__V_Map::class.java, _K_::class.javaPrimitiveType, _V_::class.javaPrimitiveType,
                { this.clear() }, // clear
                { if(it !is _K_) false else this.containsKey(it) }, // containsKey
                { callback -> this.forEachKey { callback(it); true } }, // forEachKey
                { if(it !is _K_) null else this.get(it) }, // get
                { k, v -> if(k is _K_ && v is _V_) this.put(k, v) }, // set
                { T_K__V_HashMap() } // default constructor
        )
         */

        /*
        abstractMap(T_K_ObjectMap::class.java, _K_::class.javaPrimitiveType, null,
                { this.clear() }, // clear
                { if(it !is _K_) false else this.containsKey(it) }, // containsKey
                { callback -> this.forEachKey { callback(it); true } }, // forEachKey
                { if(it !is _K_) null else this.get(it) }, // get
                { k, v -> if(k is _K_ && v is Any?) (this as T_K_ObjectMap<Any?>).put(k, v) }, // set
                { T_K_ObjectHashMap<Any?>() } // default constructor
        )
         */

        /*
        abstractMap(TObject_V_Map::class.java, null, _V_::class.javaPrimitiveType,
                { this.clear() }, // clear
                { (this as TObject_V_Map<Any?>).containsKey(it) }, // containsKey
                { callback -> this.forEachKey { callback(it); true } }, // forEachKey
                { (this as TObject_V_Map<Any?>).get(it) }, // get
                { k, v -> if(v is _V_) (this as TObject_V_Map<Any?>).put(k, v) }, // set
                { TObject_V_HashMap<Any?>() } // default constructor
        )
         */
        abstractMap(TLongFloatMap::class.java, Long::class.javaPrimitiveType, Float::class.javaPrimitiveType,
                { this.clear() }, // clear
                { if(it !is Long) false else this.containsKey(it) }, // containsKey
                { callback -> this.forEachKey { callback(it); true } }, // forEachKey
                { if(it !is Long) null else this.get(it) }, // get
                { k, v -> if(k is Long && v is Float) this.put(k, v) }, // set
                { TLongFloatHashMap() } // default constructor
        )
        abstractMap(TLongObjectMap::class.java, Long::class.javaPrimitiveType, null,
                { this.clear() }, // clear
                { if(it !is Long) false else this.containsKey(it) }, // containsKey
                { callback -> this.forEachKey { callback(it); true } }, // forEachKey
                { if(it !is Long) null else this.get(it) }, // get
                { k, v -> if(k is Long && v is Any?) (this as TLongObjectMap<Any?>).put(k, v) }, // set
                { TLongObjectHashMap<Any?>() } // default constructor
        )
    }

    fun <T: Any> abstractMap(type: Class<T>, keyType: Class<*>?, valueType: Class<*>?, clear: T.() -> Unit, containsKey: T.(Any?) -> Boolean, forEachKey: T.((Any?) -> Unit) -> Unit, get: T.(Any?) -> Any?, set: T.(Any?, Any?) -> Unit, creator: (() -> T)? = null) {
        val typeName = "trove:map.${ (keyType ?: Any::class.java).simpleName }.${ (valueType ?: Any::class.java).simpleName }"
        SerializerRegistry.register(typeName, Serializer({ type.isAssignableFrom(it.clazz) }, SerializerPriority.GENERAL))

        val keyFieldType = if (keyType == null) null else FieldTypeClass(keyType, keyType)
        val valueFieldType = if (valueType == null) null else FieldTypeClass(valueType, valueType)

        SerializerRegistry[typeName]?.register(Targets.NBT, { type ->
            var genericIndex = 0
            val keyParam = keyFieldType ?: (type as FieldTypeGeneric).generic(genericIndex++)!!
            val valueParam = valueFieldType ?: (type as FieldTypeGeneric).generic(genericIndex++)!!
            val keySerializer = SerializerRegistry.lazyImpl(Targets.NBT, keyParam)
            val valueSerializer = SerializerRegistry.lazyImpl(Targets.NBT, valueParam)

            val constructorMH: (Array<Any?>) -> T =
                try {
                    MethodHandleHelper.wrapperForConstructor<T>(type.clazz)
                } catch(e: ReflectionHelper.UnableToFindMethodException) {
                    if(creator == null)
                        throw e
                    { arr -> creator() }
                }

            Targets.NBT.impl<T>({ nbt, existing, syncing ->
                val list = nbt.safeCast(NBTTagList::class.java)
                val map = existing ?: constructorMH(arrayOf<Any?>())
                map.clear()

                list.forEachIndexed<NBTTagCompound> { i, container ->
                    val keyTag = container.getTag("k")
                    val valTag = container.getTag("v")
                    val k = if (keyTag == null) null else keySerializer().read(keyTag, null, syncing)
                    val v = if (valTag == null) null else valueSerializer().read(valTag, existing?.get(k), syncing)
                    map.set(k, v)
                }

                map
            }, { value, syncing ->
                val list = NBTTagList()

                value.forEachKey { k ->
                    val container = NBTTagCompound()
                    list.appendTag(container)
                    val v = value.get(k)

                    if (k != null) {
                        container.setTag("k", keySerializer().write(k, syncing))
                    }
                    if (v != null) {
                        container.setTag("v", valueSerializer().write(v, syncing))
                    }
                }

                list
            })
        })

        SerializerRegistry[typeName]?.register(Targets.BYTES, { type ->
            type as FieldTypeGeneric
            var genericIndex = 0
            val keyParam = keyFieldType ?: type.generic(genericIndex++)!!
            val valueParam = valueFieldType ?: type.generic(genericIndex++)!!
            val keySerializer = SerializerRegistry.lazyImpl(Targets.BYTES, keyParam)
            val valueSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, valueParam)

            val constructorMH: (Array<Any?>) -> T =
                    try {
                        MethodHandleHelper.wrapperForConstructor<T>(type.clazz)
                    } catch(e: ReflectionHelper.UnableToFindMethodException) {
                        if(creator == null)
                            throw e
                        { arr -> creator() }
                    }

            Targets.BYTES.impl<T>({ buf, existing, syncing ->
                val map = existing ?: constructorMH(arrayOf<Any?>())
                map.clear()

                val nullCount = buf.readVarInt()
                for (i in 0..nullCount - 1) {
                    val k = keySerializer().read(buf, null, syncing)
                    map.set(k, null)
                }

                val hasNullKey = buf.readBoolean()
                if (hasNullKey) {
                    val isNullValue = buf.readBoolean()
                    map.set(null, if (isNullValue) null else valueSerializer().read(buf, existing?.get(null), syncing))
                }

                val nonNullCount = buf.readVarInt()
                for (i in 0..nonNullCount - 1) {
                    val k = keySerializer().read(buf, null, syncing)
                    val v = valueSerializer().read(buf, existing?.get(k), syncing)
                    map.set(k, v)
                }

                map
            }, { buf, value, syncing ->
                val nulls = mutableSetOf<Any>()
                value.forEachKey { k ->
                    if(k == null)
                        return@forEachKey
                    if(value.get(k) == null)
                        nulls.add(k)
                }
                buf.writeVarInt(nulls.size)
                nulls.forEach { keySerializer().write(buf, it, syncing) }

                if(keyType == null) {
                    val contains = value.containsKey(null)
                    buf.writeBoolean(contains)
                    if (contains) {
                        val nullVal = value.get(null)
                        buf.writeBoolean(nullVal == null)
                        if (nullVal != null)
                            valueSerializer().write(buf, nullVal, syncing)
                    }
                }

                val nonNulls = mutableSetOf<Any>()
                value.forEachKey { k ->
                    if(k == null)
                        return@forEachKey
                    if(value.get(k) == null)
                        return@forEachKey
                    nonNulls.add(k)
                }
                buf.writeVarInt(nonNulls.size)
                nonNulls.forEach {
                    keySerializer().write(buf, it, syncing)
                    valueSerializer().write(buf, value.get(it)!!, syncing)
                }
            })
        })
    }
}
