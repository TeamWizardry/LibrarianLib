package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestObject
import net.minecraft.block.Block
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.renderer.color.ItemColors
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.Logger
import java.awt.Color

abstract class TestMod(name: String, logger: Logger): LibrarianLibModule("$name-test", logger) {

    val itemGroup = object : ItemGroup("librarianlib-$name-test.itemgroup") {
        override fun createIcon(): ItemStack {
            return ItemStack(Items.IRON_INGOT)
        }
    }

    val _items: MutableList<Item> = mutableListOf()
    val _blocks: MutableList<Block> = mutableListOf()
    val _entities: MutableList<EntityType<*>> = mutableListOf()

    val items: List<Item> = _items.unmodifiableView()
    val blocks: List<Block> = _blocks.unmodifiableView()
    val entities: List<EntityType<*>> = _entities.unmodifiableView()

    operator fun <T: Item> T.unaryPlus(): T {
        _items.add(this)
        return this
    }

    operator fun <T: Block> T.unaryPlus(): T {
        _blocks.add(this)
        return this
    }

    operator fun <T: EntityType<*>> T.unaryPlus(): T {
        _entities.add(this)
        return this
    }

    override fun setup(event: FMLCommonSetupEvent) {
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        entities.forEach {
//            RenderingRegistry.registerEntityRenderingHandler(entityClass) { ParticleSpawnerEntityRenderer(it) }
        }
    }

    @SubscribeEvent
    internal fun registerColors(colorHandlerEvent: ColorHandlerEvent.Item) {
        colorHandlerEvent.itemColors.register(IItemColor { stack, tintIndex ->
            if(tintIndex == 1 && stack.item is TestItem)
                Color.getHSBColor((stack.item.registryName.hashCode() / 1000.toFloat()) % 1, 0.8f, 0.8f).rgb
            else
                Color.WHITE.rgb
        }, *items.toTypedArray())
    }

    override fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
        blocks.forEach {
            ForgeRegistries.BLOCKS.register(it)
        }
    }

    override fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        items.forEach {
            ForgeRegistries.ITEMS.register(it)
        }
    }

    override fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
        entities.forEach {
            ForgeRegistries.ENTITIES.register(it)
        }
    }
}
