package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readBooleanArray
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.*
import com.teamwizardry.librarianlib.common.util.saving.serializers.*
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeBooleanArray
import net.minecraft.nbt.NBTTagCompound
import java.lang.reflect.Constructor
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object SerializeObject {
    init {
        SerializerRegistry.register("liblib:savable", Serializer({ type ->
            val savable = type.clazz.isAnnotationPresent(Savable::class.java)
            val inplace = ISerializeInPlace::class.java.isAssignableFrom(type.clazz)
            savable || inplace
        }))

        SerializerRegistry["liblib:savable"]?.register(Targets.NBT, { type ->

            val analysis = SerializerAnalysis(type, Targets.NBT)
            Targets.NBT.impl<Any>({ nbt, existing, sync ->
                val tag = nbt.safeCast(NBTTagCompound::class.java)

                if (analysis.mutable) {
                    val instance = existing ?: analysis.constructorMH(arrayOf())
                    analysis.alwaysFields.forEach {
                        if (tag.hasKey(it.key)) {
                            it.value.setter(instance, analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), it.value.getter(instance), sync))
                        } else {
                            it.value.setter(instance, null)
                        }
                    }
                    if (sync) {
                        analysis.noSyncFields.forEach {
                            if (tag.hasKey(it.key)) {
                                it.value.setter(instance, analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), it.value.getter(instance), sync))
                            } else {
                                it.value.setter(instance, null)
                            }
                        }
                    }
                    return@impl instance
                } else {
                    return@impl analysis.constructorMH(analysis.constructorArgOrder.map {
                        if (tag.hasKey(it))
                            analysis.serializers[it]!!.invoke().read(tag.getTag(it), null, sync)
                        else
                            null
                    }.toTypedArray())
                }
            }, { value, sync ->
                val tag = NBTTagCompound()

                analysis.alwaysFields.forEach {
                    val fieldValue = it.value.getter(value)
                    if (fieldValue != null)
                        tag.setTag(it.key, analysis.serializers[it.key]!!.invoke().write(fieldValue, sync))
                }

                if (!sync) {
                    analysis.noSyncFields.forEach {
                        val fieldValue = it.value.getter(value)
                        if (fieldValue != null)
                            tag.setTag(it.key, analysis.serializers[it.key]!!.invoke().write(fieldValue, sync))
                    }
                }
                tag
            })
        })

        SerializerRegistry["liblib:savable"]?.register(Targets.BYTES, { type ->

            val analysis = SerializerAnalysis(type, Targets.BYTES)
            val allFieldsOrdered = analysis.alwaysFields + analysis.noSyncFields
            Targets.BYTES.impl<Any>({ buf, existing, sync ->
                val nullsig = buf.readBooleanArray()
                var i = 0
                if (analysis.mutable) {
                    val instance = existing ?: analysis.constructorMH(arrayOf())
                    analysis.alwaysFields.forEach {
                        if (nullsig[i++]) {
                            it.value.setter(instance, null)
                        } else {
                            it.value.setter(instance, analysis.serializers[it.key]!!.invoke().read(buf, it.value.getter(instance), sync))
                        }
                    }
                    if (!sync) {
                        analysis.noSyncFields.forEach {
                            if (nullsig[i++]) {
                                it.value.setter(instance, null)
                            } else {
                                it.value.setter(instance, analysis.serializers[it.key]!!.invoke().read(buf, it.value.getter(instance), sync))
                            }
                        }
                    }
                    return@impl instance
                } else {
                    val map = mutableMapOf<String, Any?>()

                    analysis.alwaysFields.forEach {
                        if (!nullsig[i++]) {
                            map[it.key] = analysis.serializers[it.key]!!.invoke().read(buf, null, sync)
                        }
                    }
                    if (!sync) {
                        analysis.noSyncFields.forEach {
                            if (!nullsig[i++]) {
                                map[it.key] = analysis.serializers[it.key]!!.invoke().read(buf, null, sync)
                            }
                        }
                    }
                    return@impl analysis.constructorMH(analysis.constructorArgOrder.map {
                        map[it]
                    }.toTypedArray())
                }
            }, { buf, value, sync ->
                val nullsig = if (sync) {
                    analysis.alwaysFields.map { it.value.getter(value) == null }.toTypedArray().toBooleanArray()
                } else {
                    allFieldsOrdered.map { it.value.getter(value) == null }.toTypedArray().toBooleanArray()
                }
                buf.writeBooleanArray(nullsig)

                analysis.alwaysFields.forEach {
                    val fieldValue = it.value.getter(value)
                    if (fieldValue != null)
                        analysis.serializers[it.key]!!.invoke().write(buf, fieldValue, sync)
                }

                if (!sync) {
                    analysis.noSyncFields.forEach {
                        val fieldValue = it.value.getter(value)
                        if (fieldValue != null)
                            analysis.serializers[it.key]!!.invoke().write(buf, fieldValue, sync)
                    }
                }
            })
        })
    }
}

class SerializerAnalysis<R, W>(val type: FieldType, val target: SerializerTarget<R, W>) {
    val alwaysFields: Map<String, FieldCache>
    val noSyncFields: Map<String, FieldCache>
    val fields: Map<String, FieldCache>

    val mutable: Boolean

    val inPlaceSavable: Boolean

    val constructor: Constructor<*>
    val constructorArgOrder: List<String>
    val constructorMH: (Array<Any?>) -> Any
    val serializers: Map<String, () -> SerializerImpl<R, W>>

    init {
        val allFields = mutableMapOf<String, FieldCache>()
        allFields.putAll(SavingFieldCache.getClassFields(type.clazz))
        addSuperClass(allFields, type.clazz)
        val fields: Map<String, FieldCache> =
                if (allFields.any { it.value.meta.hasFlag(SavingFieldFlag.ANNOTATED) }) {
                    allFields.filter {
                        it.value.meta.hasFlag(SavingFieldFlag.ANNOTATED)
                    }
                } else if (type.clazz.isAnnotationPresent(Savable::class.java)) {
                    allFields.filter {
                        it.value.meta.hasFlag(SavingFieldFlag.FIELD) && !it.value.meta.hasFlag(SavingFieldFlag.TRANSIENT)
                    }
                } else {
                    mapOf<String, FieldCache>()
                }
        this.mutable = !fields.any { it.value.meta.hasFlag(SavingFieldFlag.FINAL) }
        this.fields = fields
        if (!mutable && fields.any { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) })
            throw SerializerException("Immutable type ${type.clazz.canonicalName} cannot have non-syncing fields")

        alwaysFields = fields.filter { !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }
        noSyncFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }

        inPlaceSavable = ISerializeInPlace::class.java.isAssignableFrom(type.clazz)

        constructor =
                if (inPlaceSavable) {
                    nullConstructor
                } else {
                    val test = type.clazz.declaredConstructors.map { it.parameters }
                    type.clazz.declaredConstructors.find { it.parameterCount == 0 } ?:
                            type.clazz.declaredConstructors.find {
                                val paramsToFind = HashMap(fields)
                                it.parameters.all {
                                    paramsToFind.remove(it.name)?.meta?.type?.equals(FieldType.create(it.parameterizedType)) ?: false
                                }
                            } ?:
                            throw SerializerException("Couldn't find zero-argument constructor or constructor with parameters (${fields.map { it.value.meta.type.toString() + " " + it.key }.joinToString(", ")}) for immutable type ${type.clazz.canonicalName}")
                }
        constructorArgOrder = constructor.parameters.map { it.name }
        constructorMH = if (inPlaceSavable) {
            { arr -> throw SerializerException("Cannot create instance of ISerializeInPlace") }
        } else {
            MethodHandleHelper.wrapperForConstructor(constructor)
        }

        serializers = fields.mapValues {
            val rawType = it.value.meta.type
            val fieldType = if (rawType is FieldTypeVariable) {
                if (type !is FieldTypeGeneric)
                    throw RuntimeException("What the actual hell? (variable field type in non-generic class)")
                type.generic(rawType.index)!!
            } else {
                rawType
            }
            SerializerRegistry.lazyImpl(target, fieldType)
        }
    }

    private fun addSuperClass(map: MutableMap<String, FieldCache>, clazz: Class<*>) {
        map.putAll(SavingFieldCache.getClassFields(clazz))
        if(clazz.superclass != null)
            addSuperClass(map, clazz.superclass)
    }

    companion object {
        val nullConstructor: Constructor<*> = Any::class.java.constructors.first()
    }
}
