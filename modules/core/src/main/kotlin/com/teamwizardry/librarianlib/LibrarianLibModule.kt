package com.teamwizardry.librarianlib

import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.Logger

abstract class LibrarianLibModule(val name: String, val logger: Logger) {
    val modEventBus: IEventBus = FMLKotlinModLoadingContext.get().modEventBus

    init {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        modEventBus.register(this)
    }

    @SubscribeEvent
    protected open fun setup(event: FMLCommonSetupEvent) {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    protected open fun clientSetup(event: FMLClientSetupEvent) {
    }

    protected open fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
    }

    protected open fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
    }

    protected open fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
    }

    @SubscribeEvent
    internal fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
        registerBlocks(blockRegistryEvent)
    }

    @SubscribeEvent
    internal fun onItemRegister(itemRegistryEvent: RegistryEvent.Register<Item>) {
        registerItems(itemRegistryEvent)
    }

    @SubscribeEvent
    internal fun onEntityRegister(entityRegistryEvent: RegistryEvent.Register<@JvmSuppressWildcards EntityType<*>>) {
        registerEntities(entityRegistryEvent)
    }
}
