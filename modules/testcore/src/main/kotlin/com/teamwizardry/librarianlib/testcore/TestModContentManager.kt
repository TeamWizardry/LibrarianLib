package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.content.TestConfig
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import pers.solid.brrp.v1.fabric.api.RRPCallback
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/**
 * A DSL for creating test objects. Loosely based on gradle's Kotlin DSL.
 */
public class TestModContentManager(public val modid: String, itemGroupName: String, logManager: ModLogManager) {
    private val logger = logManager.makeLogger("TestModContentManager")
    private val objects = mutableMapOf<String, TestConfig>()


    public val itemGroupKey: RegistryKey<ItemGroup> =
        RegistryKey.of(Registries.ITEM_GROUP.key, Identifier.of(modid, "item_group"))
    public val itemGroup: ItemGroup = FabricItemGroup.builder()
        .icon { ItemStack(Items.STICK) }
        .displayName(Text.translatable("itemGroup.${modid}.item_group"))
        .build()

    private val resources: TestModResourceManager = TestModResourceManager(modid, logManager)
    init {
        resources.lang.add(itemGroup, itemGroupName)
    }

    public fun registerCommon() {
        Registry.register(Registries.ITEM_GROUP, itemGroupKey, itemGroup)
        logger.info("Performing common registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerCommon(resources)
        }
    }

    public fun registerClient() {
        logger.info("Performing client registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerClient(resources)
        }
        resources.writeLang()
        RRPCallback.BEFORE_VANILLA.register {
            it.add(resources.runtimeResourcePack)
        }
    }

    public fun registerServer() {
        logger.info("Performing server registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerServer(resources)
        }
        resources.writeLang()
        RRPCallback.BEFORE_VANILLA.register {
            it.add(resources.runtimeResourcePack)
        }
    }

    public fun id(name: String): Identifier = Identifier.of(modid, name)

    public fun hasObject(name: String): Boolean {
        return objects.contains(name)
    }

    public fun <T : TestConfig> create(type: KClass<T>, name: String): T {
        objects[name]?.also {
            throw IllegalArgumentException(
                "An object named $name already exists (existing object is ${it.javaClass.canonicalName})"
            )
        }
        val value = type.primaryConstructor!!.call(this, Identifier.of(modid, name))
        objects[name] = value
        return value
    }

    public inline fun <reified T : TestConfig> create(name: String): T {
        return create(T::class, name)
    }

    public fun <T : TestConfig> create(type: KClass<T>, name: String, config: T.() -> Unit): T {
        return create(type, name).apply(config)
    }

    public inline fun <reified T : TestConfig> create(name: String, config: T.() -> Unit): T {
        return create(T::class, name).apply(config)
    }

    public fun <T : TestConfig> named(name: String): T {
        val value = objects[name] ?: throw IllegalStateException("No objects named $name exist")
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    public inline fun <T : TestConfig> named(name: String, config: T.() -> Unit): T {
        return named<T>(name).apply(config)
    }
}