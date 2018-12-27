package com.teamwizardry.librarianlib.features.animator

/**
 * A property that can be animated.
 * [keyPath] is simply a sequence of fields to follow, with additional "virtual" fields available when registered with
 * the [VirtualFieldAccessorHandler]. Call [VirtualFieldAccessorHandler.printAvailable], optionally passing it a type,
 * to see what virtual fields are available and what their types are.
 */
class AnimatableProperty<T : Any> private constructor(val target: Class<T>, val keyPath: String) {

    private val getter: (target: T) -> Any?
    private val setter: (target: T, value: Any?) -> Unit
    private val involvement: (target: T, check: Any) -> Boolean
    val type: Class<Any>

    init {
        val response = generateGetterAndSetterForKeyPath(target, keyPath.split("\\.|(?=\\[)".toRegex()).toTypedArray())
        getter = response.getter
        setter = response.setter
        type = response.clazz
        involvement = response.involvement
    }

    fun get(target: T): Any {
        return getter(target) ?: throw NullPointerException("Cannot have null array in animation!")
    }

    fun set(target: T, value: Any) {
        setter(target, value)
    }

    fun doesInvolve(target: T, obj: Any): Boolean {
        return involvement(target, obj)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimatableProperty<*>) return false

        if (target != other.target) return false
        if (keyPath != other.keyPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + keyPath.hashCode()
        return result
    }

    companion object {
        private var map = mutableMapOf<Pair<Class<*>, String>, AnimatableProperty<*>>()
        fun <T : Any> get(target: Class<T>, keyPath: String): AnimatableProperty<T> {
            @Suppress("UNCHECKED_CAST")
            return map.getOrPut(target to keyPath) { AnimatableProperty(target, keyPath) } as AnimatableProperty<T>
        }
    }
}
