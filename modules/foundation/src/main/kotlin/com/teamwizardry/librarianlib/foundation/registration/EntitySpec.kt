package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.sided.ClientFunction
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntitySize
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.network.FMLPlayMessages
import java.util.function.BiFunction

/**
 * The specs for creating and registering a tile entity.
 */
public class EntitySpec<T: Entity>(
    /**
     * The registry name, sans mod ID.
     */
    public val id: String,
    /**
     * The entity's classification
     */
    classification: EntityClassification,
    /**
     * The entity factory.
     */
    factory: EntityType.IFactory<T>
) {
    /**
     * The mod ID to register this tile entity under. This is populated by the [RegistrationManager].
     */
    public var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the tile entity type. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    public val registryName: ResourceLocation
        get() = ResourceLocation(modid, id)

    private val builder = EntityType.Builder.create(factory, classification)
    @get:JvmSynthetic
    internal var renderFactory: ClientFunction<EntityRendererManager, EntityRenderer<in T>>? = null
        private set

    public fun customClientFactory(factory: BiFunction<FMLPlayMessages.SpawnEntity, World, T>): EntitySpec<T> = build {
        builder.setCustomClientFactory(factory)
    }

    public fun renderFactory(factory: ClientFunction<EntityRendererManager, EntityRenderer<in T>>): EntitySpec<T> = build {
        this.renderFactory = factory
    }

    public fun disableSummoning(): EntitySpec<T> = build {
        builder.disableSummoning()
    }

    public fun disableSerialization(): EntitySpec<T> = build {
        builder.disableSerialization()
    }

    public fun immuneToFire(): EntitySpec<T> = build {
        builder.immuneToFire()
    }

    public fun spawningIgnoresPlayerProximity(): EntitySpec<T> = build {
        builder.func_225435_d()
    }

    public fun updateInterval(interval: Int): EntitySpec<T> = build {
        builder.setUpdateInterval(interval)
    }

    public fun trackingRange(range: Int): EntitySpec<T> = build {
        builder.setTrackingRange(range)
    }

    public fun shouldReceiveVelocityUpdates(value: Boolean): EntitySpec<T> = build {
        builder.setShouldReceiveVelocityUpdates(value)
    }

    public val typeInstance: EntityType<T> by lazy {
        @Suppress("UNCHECKED_CAST")
        builder.build(id).setRegistryName(registryName) as EntityType<T>
    }

    public val lazy: LazyEntityType<T> = LazyEntityType(this)

    private inline fun build(block: () -> Unit): EntitySpec<T> {
        block()
        return this
    }
}