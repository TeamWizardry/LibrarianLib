package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.datagen.TextureExistsExistingFileHelper
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.data.DataGenerator
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent

/**
 * The main class for registering objects in LibrarianLib Foundation. This class manages when to register them, along
 * with any secondary registrations, such as [block items][BlockItem] and block render layers.
 */
class RegistrationManager(val modid: String, modEventBus: IEventBus) {
    init {
        modEventBus.register(this)
    }

    val defaultItemGroup: ItemGroup = object: ItemGroup(modid) {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(defaultItemGroupIcon ?: Items.AIR)
        }
    }
    var defaultItemGroupIcon: Item? = null

    private val blocks = mutableListOf<BlockSpec>()
    private val items = mutableListOf<ItemSpec>()

    fun add(spec: BlockSpec): BlockSpec {
        spec.verifyComplete()
        spec.modid = modid
        blocks.add(spec)
        return spec
    }

    fun add(spec: ItemSpec): ItemSpec {
        spec.verifyComplete()
        spec.modid = modid
        items.add(spec)
        return spec
    }

    @SubscribeEvent
    internal fun registerBlocks(e: RegistryEvent.Register<Block>) {
        blocks.forEach { block ->
            e.registry.register(block.blockInstance)
        }
    }

    @SubscribeEvent
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    internal fun registerItems(e: RegistryEvent.Register<Item>) {
        blocks.forEach { block ->
            if (block.hasItem) {
                block.itemProperties.group(block.itemGroup.get(this))
                e.registry.register(block.itemInstance)
            }
        }
    }

    @SubscribeEvent
    internal fun commonSetup(e: FMLCommonSetupEvent) {
    }

    @SubscribeEvent
    internal fun clientSetup(e: FMLClientSetupEvent) {
        blocks.forEach { block ->
            RenderTypeLookup.setRenderLayer(block.blockInstance, block.renderLayer.getRenderType())
        }
    }

    @SubscribeEvent
    internal fun dedicatedServerSetup(e: FMLDedicatedServerSetupEvent) {
    }

    @SubscribeEvent
    fun gatherData(e: GatherDataEvent) {
        e.generator.addProvider(BlockStates(e.generator, modid, e.existingFileHelper))
    }

    private inner class BlockStates(gen: DataGenerator, modid: String, exFileHelper: ExistingFileHelper)
        : BlockStateProvider(gen, modid, TextureExistsExistingFileHelper(exFileHelper)) {
        override fun registerStatesAndModels() {
            blocks.forEach {
                (it.blockInstance as? IFoundationBlock)?.generateBlockState(this)
            }
        }
    }
}
