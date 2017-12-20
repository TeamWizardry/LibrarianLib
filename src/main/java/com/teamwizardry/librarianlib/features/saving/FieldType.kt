package com.teamwizardry.librarianlib.features.saving

import com.google.common.reflect.TypeToken
import com.google.gson.internal.`$Gson$Types`
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.*
import java.util.*

val getGenericSuperclassMH = MethodHandleHelper.wrapperForStaticMethod(`$Gson$Types`::class.java, "getGenericSupertype", null, Type::class.java, Class::class.java, Class::class.java)

abstract class FieldType protected constructor(val type: Type, annotated: AnnotatedType?, open val clazz: Class<*>) {
    val annotations: Array<Annotation> = annotated?.annotations ?: emptyArray()
    protected val annotString: String
        get() {
            return annotations.joinToString(" ") {
                "@" + it.annotationClass.simpleName
            } + " "
        }

    open val interfaces: Array<out Class<*>>
        get() = arrayOf()

    fun resolve(type: Type, annotated: AnnotatedType?): FieldType {
        return FieldType.create(`$Gson$Types`.resolve(this.type, this.clazz, type), annotated)
    }

    fun resolveGeneric(iface: Class<*>, index: Int): FieldType {
        val superclass = this.genericSuperclass(iface) as FieldTypeGeneric
        return this.resolve(superclass.generic(index).type, null) // TODO: Implement supertype annotations
    }

    fun genericSuperclass(clazz: Class<*>): FieldType {
        return FieldType.create(getGenericSuperclassMH(arrayOf(this.type, this.clazz, clazz)) as Type, null) // TODO: Implement supertype annotations
    }

    companion object {
        @JvmStatic
        fun create(field: Field) = create(field.genericType, field.annotatedType)

        @JvmStatic
        fun create(method: Method) = create(method.genericReturnType, method.annotatedReturnType)

        @JvmStatic
        fun create(token: TypeToken<*>) = create(token.type, null)

        @JvmStatic
        fun create(clazz: Class<*>) = create(clazz, null)

        @JvmStatic
        fun create(type: Type, annots: AnnotatedType?): FieldType {
            val fType: FieldType =
                    if (type is ParameterizedType)
                        createGeneric(type, annots)
                    else if (type is GenericArrayType)
                        createGenericArray(type, annots)
                    else if (type is TypeVariable<*>)
                        createVariable(type, annots)
                    else if (type is Class<*>)
                        if (type.isArray)
                            createArray(type, annots)
                        else
                            createPlain(type, annots)
                    else
                        FieldTypeError(type)

            return fType
        }

        private fun createPlain(type: Class<*>, annots: AnnotatedType?): FieldType {
            return FieldTypeClass(type, annots, type)
        }

        private fun createArray(type: Class<*>, annots: AnnotatedType?): FieldType {
            val component = if (annots is AnnotatedArrayType) {
                val a = annots.annotatedGenericComponentType
                create(a.type, a)
            } else {
                create(type.componentType, null)
            }
            return FieldTypeArray(type, annots, component)
        }

        @Suppress("IfThenToElvis")
        private fun createGeneric(type: ParameterizedType, annots: AnnotatedType?): FieldType {
            val args = if (annots is AnnotatedParameterizedType) {
                annots.annotatedActualTypeArguments.map { create(it.type, it) }
            } else {
                type.actualTypeArguments.map { create(it, null) }
            }
            return FieldTypeGeneric(type, annots, type.rawType as Class<*>, args.toTypedArray())
        }

        private fun createGenericArray(type: GenericArrayType, annots: AnnotatedType?): FieldType {
            val component = if (annots is AnnotatedArrayType) {
                val a = annots.annotatedGenericComponentType
                create(a.type, a)
            } else {
                create(type.genericComponentType, null)
            }
            return FieldTypeArray(type, annots, component)
        }

        private fun createVariable(type: TypeVariable<*>, annots: AnnotatedType?): FieldType {
            return FieldTypeVariable(type, annots, type.genericDeclaration as Class<*>, type.name, type.genericDeclaration.typeParameters.indexOfFirst { it.name == type.name })
        }
    }
}

class FieldTypeError(type: Type) : FieldType(type, null, Any::class.java) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is FieldType) return false
        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return "ERR(" + type.toString() + ")"
    }
}

class FieldTypeClass(type: Type, annots: AnnotatedType?, clazz: Class<*>) : FieldType(type, annots, clazz) {

    override val interfaces: Array<out Class<*>> = if (clazz.isInterface) arrayOf(*clazz.interfaces, clazz) else clazz.interfaces

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeClass) return false

        if (clazz != other.clazz) return false

        return true
    }

    override fun hashCode(): Int {
        return clazz.hashCode()
    }

    override fun toString(): String {
        return annotString + clazz.simpleName
    }
}

class FieldTypeArray(type: Type, annots: AnnotatedType?, val componentType: FieldType) : FieldType(type, annots, getArrayType(componentType)) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeArray) return false

        if (componentType != other.componentType) return false

        return true
    }

    override fun hashCode(): Int {
        return componentType.hashCode()
    }

    override fun toString(): String {
        return componentType.toString() + annotString + "[]"
    }

    companion object {
        private fun getArrayType(componentType: FieldType): Class<*> {
            return java.lang.reflect.Array.newInstance(componentType.clazz, 0).javaClass
        }
    }
}

class FieldTypeGeneric(type: Type, annots: AnnotatedType?, clazz: Class<*>, val generics: Array<FieldType>) : FieldType(type, annots, clazz) {

    fun generic(i: Int): FieldType {
        return generics[i]
    }

    fun genericOrNull(i: Int): FieldType? {
        if (i < 0 || i >= generics.size)
            return null
        return generics[i]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeGeneric) return false

        if (clazz != other.clazz) return false
        if (!Arrays.equals(generics, other.generics)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + Arrays.hashCode(generics)
        return result
    }

    override fun toString(): String {
        return annotString + clazz.simpleName + "<" + generics.map { it.toString() }.joinToString(", ") + ">"
    }
}

class FieldTypeVariable(type: Type, annots: AnnotatedType?, val parent: Class<*>, val name: String, val index: Int) : FieldType(type, annots, Any::class.java) {
    override val clazz: Class<*>
        get() = throw UnsupportedOperationException("Cannot get class from variable field type!")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeVariable) return false

        if (name != other.name) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + index
        return result
    }

    override fun toString(): String {
        return annotString + name
    }
}
