package com.teamwizardry.librarianlib.features.animator

/**
 * A property that can be animated.
 * [keyPath] is simply a sequence of fields to follow, with additional "virtual" fields available when registered with
 * the [VirtualFieldAccessorHandler]. Call [VirtualFieldAccessorHandler.printAvailable], optionally passing it a type,
 * to see what virtual fields are available and what their types are.
 */
class AnimatableProperty<T : Any> private constructor(val target: Class<T>, val keyPath: String) : IAnimatable<T> {

    private val getter: (target: T) -> Any?
    private val setter: (target: T, value: Any?) -> Unit
    private val involvement: (target: T, check: Any) -> Boolean
    override val type: Class<Any>

    init {
        val response = generateGetterAndSetterForKeyPath(target, keyPath.split("\\.|(?=\\[)".toRegex()).toTypedArray())
        getter = response.getter
        setter = response.setter
        type = response.clazz
        involvement = response.involvement
    }

    override fun get(target: T) =
            getter(target) ?: throw NullPointerException("Cannot have null value in animation!")

    override fun set(target: T, value: Any) = setter(target, value)

    override fun doesInvolve(target: T, obj: Any) = involvement(target, obj)

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

/**
 * A static property that can be animated.
 * [keyPath] is simply a sequence of fields to follow, with additional "virtual" fields available when registered with
 * the [VirtualFieldAccessorHandler]. Call [VirtualFieldAccessorHandler.printAvailable], optionally passing it a type,
 * to see what virtual fields are available and what their types are.
 */
class StaticAnimatableProperty<T : Any> private constructor(val target: Class<T>, val keyPath: String) : IAnimatable<Nothing?> {

    private val getter: () -> Any?
    private val setter: (value: Any?) -> Unit
    private val involvement: (check: Any) -> Boolean
    override val type: Class<Any>

    init {
        val response = generateGetterAndSetterForStaticKeyPath(target, keyPath.split("\\.|(?=\\[)".toRegex()).toTypedArray())
        getter = response.getter
        setter = response.setter
        type = response.clazz
        involvement = response.involvement
    }

    override fun get(target: Nothing?) =
            getter() ?: throw NullPointerException("Cannot have null value in animation!")

    override fun set(target: Nothing?, value: Any) = setter(target)

    override fun doesInvolve(target: Nothing?, obj: Any) = involvement(obj)

    companion object {
        private var map = mutableMapOf<Pair<Class<*>, String>, StaticAnimatableProperty<*>>()
        fun <T : Any> get(target: Class<T>, keyPath: String): StaticAnimatableProperty<T> {
            @Suppress("UNCHECKED_CAST")
            return map.getOrPut(target to keyPath) { StaticAnimatableProperty(target, keyPath) } as StaticAnimatableProperty<T>
        }
    }
}
