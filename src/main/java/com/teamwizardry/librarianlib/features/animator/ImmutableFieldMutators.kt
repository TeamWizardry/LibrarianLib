package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.animator.internal.VecMutators

/**
 * TODO: Document file ImmutableFieldMutators
 *
 * Created by TheCodeWarrior
 */
object ImmutableFieldMutatorHandler {
    private val map = mutableMapOf<Pair<Class<*>, String>, ImmutableFieldMutator<*>?>()
    private val providers = mutableMapOf<Class<*>, ImmutableFieldMutatorProvider<*>>()

    fun <T> registerProvider(clazz: Class<T>, provider: ImmutableFieldMutatorProvider<T>) {
        providers.put(clazz, provider)
    }

    fun getMutator(clazz: Class<*>, field: String): ImmutableFieldMutator<Any>? {
        val key = clazz to field
        if(key !in map) {
            map[key] = providers[clazz]?.getMutatorForImmutableField(field)
        }
        @Suppress("UNCHECKED_CAST")
        return map[key] as ImmutableFieldMutator<Any>?
    }

    init {
        VecMutators
    }
}

interface ImmutableFieldMutatorProvider<T> {
    fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<T>?
}

@FunctionalInterface
interface ImmutableFieldMutator<T> {
    fun mutate(target: T, value: Any?): T
}
