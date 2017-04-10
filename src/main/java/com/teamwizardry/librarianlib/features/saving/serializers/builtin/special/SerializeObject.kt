package com.teamwizardry.librarianlib.features.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.kotlin.writeBooleanArray
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.*
import com.teamwizardry.librarianlib.features.saving.serializers.*
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTTagCompound
import java.lang.reflect.Constructor
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object SerializeObject {

    fun inPlaceCheck(clazz: Class<*>): Boolean {
        if (clazz.isAnnotationPresent(SaveInPlace::class.java))
            return true
        return clazz.superclass?.let { inPlaceCheck(it) } ?: false
    }

    init {
        SerializerRegistry.register("liblib:savable", Serializer({ type ->
            val savable = type.clazz.isAnnotationPresent(Savable::class.java)
            val inplace = inPlaceCheck(type.clazz)
            savable || inplace
        }, SerializerPriority.GENERAL))

        SerializerRegistry["liblib:savable"]?.register(Targets.NBT, { type ->

            val analysis = SerializerAnalysis(type, Targets.NBT)
            Targets.NBT.impl<Any>({ nbt, existing, sync ->
                val tag = nbt.safeCast(NBTTagCompound::class.java)

                if (analysis.mutable) {
                    val instance = existing ?: analysis.constructorMH(arrayOf())
                    analysis.alwaysFields.forEach {
                        val value = if (tag.hasKey(it.key)) {
                            analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), it.value.getter(instance), sync)
                        } else {
                            null
                        }
                        if (!it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                            it.value.setter(instance, value)
                        }
                    }
                    if (sync) {
                        analysis.noSyncFields.forEach {
                            val value = if (tag.hasKey(it.key)) {
                                analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), it.value.getter(instance), sync)
                            } else {
                                null
                            }
                            if (!it.value.meta.hasFlag(SavingFieldFlag.FINAL))
                                it.value.setter(instance, value)
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
                        val value = if (nullsig[i++]) {
                            null
                        } else {
                            analysis.serializers[it.key]!!.invoke().read(buf, it.value.getter(instance), sync)
                        }
                        if (!it.value.meta.hasFlag(SavingFieldFlag.FINAL))
                            it.value.setter(instance, value)
                    }
                    if (!sync) {
                        analysis.noSyncFields.forEach {
                            val value = if (nullsig[i++]) {
                                null
                            } else {
                                analysis.serializers[it.key]!!.invoke().read(buf, it.value.getter(instance), sync)
                            }
                            if (!it.value.meta.hasFlag(SavingFieldFlag.FINAL))
                                it.value.setter(instance, value)
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
        this.fields =
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
        inPlaceSavable = SerializeObject.inPlaceCheck(type.clazz)
        this.mutable = inPlaceSavable || !fields.any { it.value.meta.hasFlag(SavingFieldFlag.FINAL) }
        if (!mutable && fields.any { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) })
            throw SerializerException("Immutable type ${type.clazz.canonicalName} cannot have non-syncing fields")

        alwaysFields = fields.filter { !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }
        noSyncFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }

        constructor =
                if (inPlaceSavable) {
                    nullConstructor
                } else {
                    type.clazz.declaredConstructors.find { it.parameterCount == 0 } ?:
                            type.clazz.declaredConstructors.find {
                                val paramsToFind = HashMap(fields)
                                val customParamNames = it.getDeclaredAnnotation(SavableConstructorOrder::class.java)?.params
                                var i = 0
                                it.parameters.all {
                                    val ret =
                                            if (customParamNames != null && i < customParamNames.size)
                                                paramsToFind.remove(customParamNames[i])?.meta?.type?.equals(FieldType.create(it.parameterizedType)) ?: false
                                            else
                                                paramsToFind.remove(it.name)?.meta?.type?.equals(FieldType.create(it.parameterizedType)) ?: false
                                    i++
                                    ret
                                }
                            } ?:
                            throw SerializerException("Couldn't find zero-argument constructor or constructor with parameters (${fields.map { it.value.meta.type.toString() + " " + it.key }.joinToString(", ")}) for immutable type ${type.clazz.canonicalName}")
                }
        constructorArgOrder = constructor.getDeclaredAnnotation(SavableConstructorOrder::class.java)?.params?.asList() ?: constructor.parameters.map { it.name }
        constructorMH = if (inPlaceSavable) {
            { throw SerializerException("Cannot create instance of class marked with @SaveInPlace") }
        } else {
            MethodHandleHelper.wrapperForConstructor(constructor)
        }

        serializers = fields.mapValues {
            val rawType = it.value.meta.type
            val fieldType = if (rawType is FieldTypeVariable) {
                if (type !is FieldTypeGeneric)
                    throw RuntimeException("Sorry, generic inheritence isn't currently implemented.")
                type.generic(rawType.index)!!
            } else {
                rawType
            }
            SerializerRegistry.lazyImpl(target, fieldType)
        }
    }

    private fun addSuperClass(map: MutableMap<String, FieldCache>, clazz: Class<*>) {
        map.putAll(SavingFieldCache.getClassFields(clazz))
        if (clazz.superclass != null)
            addSuperClass(map, clazz.superclass)
    }

    companion object {
        val nullConstructor: Constructor<*> = Any::class.java.constructors.first()
    }
}
