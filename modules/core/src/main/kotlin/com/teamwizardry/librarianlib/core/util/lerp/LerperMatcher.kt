package com.teamwizardry.librarianlib.core.util.lerp

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import dev.thecodewarrior.mirror.type.ConcreteTypeMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.mirror.type.VariableMirror
import dev.thecodewarrior.mirror.type.WildcardMirror
import java.lang.IllegalArgumentException

/**
 * Yoinked from [Prism](https://github.com/thecodewarrior/Prism/blob/bc714f6/core/src/main/kotlin/dev/thecodewarrior/prism/Prism.kt)
 */
public open class LerperMatcher {
    private val _lerpers = mutableMapOf<TypeMirror, Lazy<Lerper<*>>>()
    public val lerpers: Map<TypeMirror, Lazy<Lerper<*>>> = _lerpers.unmodifiableView()

    private val _factories = mutableListOf<LerperFactory<Lerper<*>>>()
    public val factories: List<LerperFactory<Lerper<*>>> = _factories.unmodifiableView()

    public fun getOrNull(mirror: TypeMirror): Lazy<Lerper<*>>? {
        try {
            return get(mirror)
        } catch(e: InvalidTypeException) {
            return null
        } catch(e: LerperNotFoundException) {
            return null
        }
    }

    public operator fun get(mirror: TypeMirror): Lazy<Lerper<*>> {
        @Suppress("NAME_SHADOWING")
        val mirror =
            if(mirror is VariableMirror) {
                throw InvalidTypeException("Type variable $mirror can't be lerped")
            } else if(mirror is WildcardMirror) {
                mirror.upperBound ?: throw InvalidTypeException("Wildcard $mirror can't be lerped since it has no upper bound")
            } else {
                mirror
            }

        _lerpers[mirror]?.also {
            return it
        }
        val unannotated = mirror.withTypeAnnotations(listOf())
        _lerpers[unannotated]?.also {
            _lerpers[mirror] = it
            return it
        }

        mirror as ConcreteTypeMirror

        val factory = _factories.fold<LerperFactory<Lerper<*>>, LerperFactory<Lerper<*>>?>(null) { acc, factory ->
            val applicable = factory.pattern.isAssignableFrom(mirror) && factory.predicate?.invoke(mirror) != false
            if (applicable) {
                val moreSpecific = acc == null || acc.pattern.specificity <= factory.pattern.specificity ||
                    (acc.predicate == null && factory.predicate != null && acc.pattern.specificity.compareTo(factory.pattern.specificity) == 0)
                if (moreSpecific)
                    factory
                else
                    acc
            } else {
                acc
            }
        }
            ?: throw LerperNotFoundException("Could not find a lerper or factory for $mirror")

        val lazy = lazy { factory.create(mirror) }
        _lerpers[mirror] = lazy
        return lazy
    }

    public fun register(vararg factories: LerperFactory<Lerper<*>>): LerperMatcher {
        factories.forEach { factory ->
            _factories.removeIf { it === factory }
            _factories.add(factory)
        }
        return this
    }

    public fun register(vararg lerpers: Lerper<*>): LerperMatcher {
        lerpers.forEach { lerper ->
            if(lerper.type in _lerpers)
                throw IllegalArgumentException("Duplicate lerper for ${lerper.type}")
            _lerpers[lerper.type] = lazyOf(lerper)
        }
        return this
    }
}
