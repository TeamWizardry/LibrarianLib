package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special

import com.google.gson.internal.`$Gson$Types`
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
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
                        val oldValue = it.value.getter(instance)
                        val value = if (tag.hasKey(it.key)) {
                            analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), oldValue, sync)
                        } else {
                            if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) oldValue else null
                        }
                        if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                            if(oldValue !== value) {
                                throw SerializerException("Cannot set final field ${it.value.name} in class $type to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                            }
                        } else {
                            it.value.setter(instance, value)
                        }
                    }
                    if (!sync) {
                        analysis.noSyncFields.forEach {
                            val oldValue = it.value.getter(instance)
                            val value = if (tag.hasKey(it.key)) {
                                analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), oldValue, sync)
                            } else {
                                if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) oldValue else null
                            }
                            if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                                if(oldValue !== value) {
                                    throw SerializerException("Cannot set final field ${it.value.name} in class $type to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                                }
                            } else {
                                it.value.setter(instance, value)
                            }
                        }
                    } else {
                        analysis.nonPersistentFields.forEach {
                            val oldValue = it.value.getter(instance)
                            val value = if (tag.hasKey(it.key)) {
                                analysis.serializers[it.key]!!.invoke().read(tag.getTag(it.key), oldValue, sync)
                            } else {
                                if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) oldValue else null
                            }
                            if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                                if(oldValue !== value) {
                                    throw SerializerException("Cannot set final field ${it.value.name} in class $type to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                                }
                            } else {
                                it.value.setter(instance, value)
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
                } else {
                    analysis.nonPersistentFields.forEach {
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
            val allFieldsOrdered = analysis.alwaysFields + analysis.noSyncFields + analysis.nonPersistentFields
            Targets.BYTES.impl<Any>({ buf, existing, sync ->
                val nullsig = buf.readBooleanArray()
                var i = 0
                if (analysis.mutable) {
                    val instance = existing ?: analysis.constructorMH(arrayOf())
                    analysis.alwaysFields.forEach {
                        val oldValue = it.value.getter(instance)
                        val value = if (nullsig[i++]) {
                            null
                        } else {
                            analysis.serializers[it.key]!!.invoke().read(buf, oldValue, sync)
                        }
                        if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                            if(oldValue !== value) {
                                throw SerializerException("Cannot set final field ${it.value.name} in class ${type} to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                            }
                        } else {
                            it.value.setter(instance, value)
                        }
                    }
                    if (!sync) {
                        analysis.noSyncFields.forEach {
                            val oldValue = it.value.getter(instance)
                            val value = if (nullsig[i++]) {
                                null
                            } else {
                                analysis.serializers[it.key]!!.invoke().read(buf, oldValue, sync)
                            }
                            if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                                if(oldValue !== value) {
                                    throw SerializerException("Cannot set final field ${it.value.name} in class ${type} to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                                }
                            } else {
                                it.value.setter(instance, value)
                            }
                        }
                    } else {
                        analysis.nonPersistentFields.forEach {
                            val oldValue = it.value.getter(instance)
                            val value = if (nullsig[i++]) {
                                null
                            } else {
                                analysis.serializers[it.key]!!.invoke().read(buf, oldValue, sync)
                            }
                            if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                                if(oldValue !== value) {
                                    throw SerializerException("Cannot set final field ${it.value.name} in class ${type} to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                                }
                            } else {
                                it.value.setter(instance, value)
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
                    } else {
                        analysis.nonPersistentFields.forEach {
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
                } else {
                    analysis.nonPersistentFields.forEach {
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
    val nonPersistentFields: Map<String, FieldCache>
    val fields: Map<String, FieldCache>

    val mutable: Boolean

    val inPlaceSavable: Boolean

    val constructor: Constructor<*>
    val constructorArgOrder: List<String>
    val constructorMH: (Array<Any?>) -> Any
    val serializers: Map<String, () -> SerializerImpl<R, W>>

    init {
        val allFields = mutableMapOf<String, FieldCache>()
        addFieldsRecursive(allFields, type)
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

        alwaysFields = fields.filter { !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) && !it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) }
        noSyncFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) && !it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) }
        nonPersistentFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) && !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }

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
                                    if(customParamNames != null && i < customParamNames.size)
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
            { arr -> throw SerializerException("Cannot create instance of class marked with @SaveInPlace") }
        } else {
            MethodHandleHelper.wrapperForConstructor(constructor)
        }

        serializers = fields.mapValues {
            val fieldType = it.value.meta.type
            SerializerRegistry.lazyImpl(target, fieldType)
        }
    }

    private tailrec fun addFieldsRecursive(map: MutableMap<String, FieldCache>, type: FieldType) {
        map.putAll(SavingFieldCache.getClassFields(type))
        if (type.clazz != Any::class.java)
            addFieldsRecursive(map, FieldType.create(`$Gson$Types`.resolve(type.type, type.clazz, type.clazz.genericSuperclass)))
    }

    companion object {
        val nullConstructor: Constructor<*> = Any::class.java.constructors.first()
    }
}
