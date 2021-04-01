package com.teamwizardry.gradle.util

import org.gradle.api.*
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

@Suppress("UnstableApiUsage")
class DslContext(val project: Project) {
    val objects: ObjectFactory
        get() = project.objects

    /**
     * Creates a simple immutable [Named] object of the given type and name.
     *
     * @param T The type of object to create
     * @param name The name of the created object
     * @return the created named object
     *
     * @see [ObjectFactory.named]
     */
    inline fun <reified T : Named> named(name: String): T =
        objects.named(T::class.java, name)


    /**
     * Create a new instance of `T`, using [parameters] as the construction parameters.
     *
     * @param T The type of object to create
     * @param parameters The construction parameters
     * @return the created named object
     *
     * @see [ObjectFactory.newInstance]
     */
    inline fun <reified T> newInstance(vararg parameters: Any): T =
        objects.newInstance(T::class.java, *parameters)


    /**
     * Creates a [Property] that holds values of the given type [T].
     *
     * @see [ObjectFactory.property]
     */
    inline fun <reified T> property(): Property<T> =
        objects.property(T::class.java)

    /**
     * Creates a [Property] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.property]
     */
    inline fun <reified T> property(noinline convention: () -> T): Property<T> =
        objects.property(T::class.java).convention(project.provider(convention))

    /**
     * Creates a [Property] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.property]
     */
    inline fun <reified T> property(convention: T): Property<T> =
        objects.property(T::class.java).convention(convention)

    /**
     * Creates a [SetProperty] that holds values of the given type [T].
     *
     * @see [ObjectFactory.setProperty]
     */
    inline fun <reified T> setProperty(): SetProperty<T> =
        objects.setProperty(T::class.java)

    /**
     * Creates a [SetProperty] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.setProperty]
     */
    inline fun <reified T> setProperty(noinline convention: () -> Set<T>): SetProperty<T> =
        objects.setProperty(T::class.java).convention(project.provider(convention))

    /**
     * Creates a [SetProperty] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.setProperty]
     */
    inline fun <reified T> setProperty(convention: Set<T>): SetProperty<T> =
        objects.setProperty(T::class.java).convention(convention)

    /**
     * Creates a [ListProperty] that holds values of the given type [T].
     *
     * @see [ObjectFactory.listProperty]
     */
    inline fun <reified T> listProperty(): ListProperty<T> =
        objects.listProperty(T::class.java)

    /**
     * Creates a [ListProperty] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.listProperty]
     */
    inline fun <reified T> listProperty(noinline convention: () -> List<T>): ListProperty<T> =
        objects.listProperty(T::class.java).convention(project.provider(convention))

    /**
     * Creates a [ListProperty] that holds values of the given type [T] with the given convention.
     *
     * @see [ObjectFactory.listProperty]
     */
    inline fun <reified T> listProperty(convention: List<T>): ListProperty<T> =
        objects.listProperty(T::class.java).convention(convention)

    /**
     * Creates a [MapProperty] that holds values of the given key type [K] and value type [V].
     *
     * @see [ObjectFactory.mapProperty]
     */
    inline fun <reified K, reified V> mapProperty(): MapProperty<K, V> =
        objects.mapProperty(K::class.java, V::class.java)

    /**
     * Creates a [MapProperty] that holds values of the given key type [K] and value type [V] with the given convention.
     *
     * @see [ObjectFactory.mapProperty]
     */
    inline fun <reified K, reified V> mapProperty(noinline convention: () -> Map<K, V>): MapProperty<K, V> =
        objects.mapProperty(K::class.java, V::class.java).convention(project.provider(convention))

    /**
     * Creates a [MapProperty] that holds values of the given key type [K] and value type [V] with the given convention.
     *
     * @see [ObjectFactory.mapProperty]
     */
    inline fun <reified K, reified V> mapProperty(convention: Map<K, V>): MapProperty<K, V> =
        objects.mapProperty(K::class.java, V::class.java).convention(convention)

    /**
     * Creates a new [NamedDomainObjectContainer] for managing named objects of the specified type.
     *
     * The specified element type must have a public constructor which takes the name as a String parameter. The type must be non-final and a class or abstract class. Interfaces are currently not supported.
     *
     * All objects **MUST** expose their name as a bean property called "name". The name must be constant for the life of the object.
     *
     * The objects created by the container are decorated and extensible, and have services available for injection. See [.newInstance] for more details.
     *
     * @param <T> The type of objects for the container to contain.
     * @return The container. Never returns null.
     * @since 5.5
     */
    inline fun <reified T> domainObjectContainer(): NamedDomainObjectContainer<T> =
        objects.domainObjectContainer(T::class.java)

    /**
     * Creates a new [NamedDomainObjectContainer] for managing named objects of the specified type. The given factory is used to create object instances.
     *
     * All objects **MUST** expose their name as a bean property named "name". The name must be constant for the life of the object.
     *
     * @param factory The factory to use to create object instances.
     * @param <T> The type of objects for the container to contain.
     * @return The container. Never returns null.
     * @since 5.5
     */
    inline fun <reified T> domainObjectContainer(factory: NamedDomainObjectFactory<T>): NamedDomainObjectContainer<T> =
        objects.domainObjectContainer(T::class.java, factory)

    /**
     * Creates a new [ExtensiblePolymorphicDomainObjectContainer] for managing named objects of the specified type.
     *
     * The returned container will not have any factories or bindings registered.
     *
     * @param <T> The type of objects for the container to contain.
     * @return The container.
     * @since 6.1
     */
    inline fun <reified T> polymorphicDomainObjectContainer(): ExtensiblePolymorphicDomainObjectContainer<T> =
        objects.polymorphicDomainObjectContainer(T::class.java)

    /**
     * Creates a new [DomainObjectSet] for managing objects of the specified type.
     *
     * @param <T> The type of objects for the domain object set to contain.
     * @return The domain object set. Never returns null.
     * @since 5.5
     */
    @Incubating
    inline fun <reified T> domainObjectSet(): DomainObjectSet<T> =
        objects.domainObjectSet(T::class.java)

    /**
     * Creates a new [NamedDomainObjectSet] for managing named objects of the specified type.
     *
     * All objects **MUST** expose their name as a bean property called "name". The name must be constant for the life of the object.
     *
     * @param <T> The type of objects for the domain object set to contain.
     * @return The domain object set.
     * @since 6.1
     */
    @Incubating
    inline fun <reified T> namedDomainObjectSet(): NamedDomainObjectSet<T> =
        objects.namedDomainObjectSet(T::class.java)

    /**
     * Creates a new [NamedDomainObjectList] for managing named objects of the specified type.
     *
     * All objects **MUST** expose their name as a bean property called "name". The name must be constant for the life of the object.
     *
     * @param <T> The type of objects for the domain object set to contain.
     * @return The domain object list.
     * @since 6.1
     */
    @Incubating
    inline fun <reified T> namedDomainObjectList(): NamedDomainObjectList<T> =
        objects.namedDomainObjectList(T::class.java)

}