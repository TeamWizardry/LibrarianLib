package com.teamwizardry.librarianlib.features.properties

import com.teamwizardry.librarianlib.features.properties.context.IPropertyContext

/**
 * Properties are one of Wire's fever dreams.
 *
 * These things are the equivalent of IMC, except... really weird.
 *
 * If you, for example, want to have a feature that people can
 * sign up for in your mod without dependency, you can use a property.
 *
 * [apply] on an object registers it for the property.
 * What this does is ENTIRELY implementation dependent.
 * The context consumer allows you to check if you're applying to the object in that state.
 * Again, the class of the context consumer is implementation dependant.
 *
 */
abstract class ModProperty {

    companion object {

        private object NoProperty : ModProperty() {
            override fun apply(obj: Any, contextConsumer: (IPropertyContext) -> Boolean) = null
        }

        private val properties = mutableMapOf<String, ModProperty>()

        @JvmStatic
        operator fun get(name: String) = properties[name] ?: NoProperty

        @JvmStatic
        fun registerProperty(name: String, property: ModProperty) = properties.put(name, property)
    }

    protected data class Registration(val obj: Any, val contextConsumer: (IPropertyContext) -> Boolean)

    protected val registrations = mutableMapOf<Any, Registration>()

    @JvmOverloads
    open fun apply(obj: Any, contextConsumer: (IPropertyContext) -> Boolean = { true }) =
            registrations.put(obj, Registration(obj, contextConsumer))?.obj
}
