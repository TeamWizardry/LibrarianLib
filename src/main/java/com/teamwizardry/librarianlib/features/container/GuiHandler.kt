package com.teamwizardry.librarianlib.features.container

import com.google.common.collect.HashBiMap
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.base.item.ItemModBook
import com.teamwizardry.librarianlib.features.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.features.kotlin.getTileEntitySafely
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * Created by TheCodeWarrior
 */
object GuiHandler : IGuiHandler {

    private val registry = mutableMapOf<ResourceLocation, GuiEntry>()
    private val ids = HashBiMap.create<ResourceLocation, Int>()

    init {
        registerRaw(ResourceLocation(LibrarianLib.MODID, "book"), null) { player, world, _ ->
            val pair = getStack<ItemModBook>(player)
            if (pair == null) null else {
                val (item, stack) = pair
                item.createGui(player, world, stack) as GuiScreen
            }
        }
    }

    private inline fun <reified T : Any> getStack(p: EntityPlayer): Pair<T, ItemStack>? {
        var target: T? = tFromStack(p.heldItemMainhand)
        if (target != null)
            return target to p.heldItemMainhand
        target = tFromStack(p.heldItemOffhand)
        if (target != null)
            return target to p.heldItemOffhand

        return null
    }

    private inline fun <reified T : Any> tFromStack(stack: ItemStack): T? {
        val item = stack.item
        return if (item is T)
            item
        else if (item is ItemBlock) {
            val block = item.block
            if (block is T)
                block
            else null
        } else null
    }

    @JvmStatic
    @JvmOverloads
    fun open(name: ResourceLocation, player: EntityPlayer, pos: BlockPos = BlockPos.ORIGIN) {
        if (name !in ids)
            throw IllegalArgumentException("No GUI handler registered for $name")
        player.openGui(LibrarianLib, ids[name]!!, player.world, pos.x, pos.y, pos.z)
    }

    @JvmStatic
    fun registerRaw(name: ResourceLocation,
                    server: ((player: EntityPlayer, world: World, pos: BlockPos) -> ContainerBase?)?,
                    client: ((player: EntityPlayer, world: World, pos: BlockPos) -> GuiScreen?)?) {
        if (name !in ids.keys) {
            ids[name] = ids.size
        }
        if (name in registry) {
            throw IllegalArgumentException("GUI handler for $name already exists")
        }
        registry[name] = GuiEntry(server, client)
    }

    @JvmStatic
    /**
     * ## Facade equivalent: [registerBasicFacadeContainer]
     */
    @Deprecated("As of version 4.20 this has been superseded by Facade")
    fun <T : ContainerBase> registerBasicContainer(name: ResourceLocation,
                                                   server: (player: EntityPlayer, pos: BlockPos, te: TileEntity?) -> T,
                                                   client: (player: EntityPlayer, container: T) -> com.teamwizardry.librarianlib.features.guicontainer.GuiContainerBase) {
        val rawServer: (EntityPlayer, World, BlockPos) -> T = { player, world, pos ->
            server(player, pos, world.getTileEntitySafely(pos))
        }

        val rawClient: (EntityPlayer, World, BlockPos) -> com.teamwizardry.librarianlib.features.guicontainer.GuiContainerBase = { player, world, pos ->
            client(player, rawServer(player, world, pos))
        }

        registerRaw(name, rawServer, rawClient)
    }

    @JvmStatic
    fun <T : ContainerBase> registerBasicFacadeContainer(name: ResourceLocation,
        server: (player: EntityPlayer, pos: BlockPos, te: TileEntity?) -> T,
        client: (player: EntityPlayer, container: T) -> com.teamwizardry.librarianlib.features.facadecontainer.GuiContainerBase) {
        val rawServer: (EntityPlayer, World, BlockPos) -> T = { player, world, pos ->
            server(player, pos, world.getTileEntitySafely(pos))
        }

        val rawClient: (EntityPlayer, World, BlockPos) -> com.teamwizardry.librarianlib.features.facadecontainer.GuiContainerBase =
            { player, world, pos ->
                client(player, rawServer(player, world, pos))
            }

        registerRaw(name, rawServer, rawClient)
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID in ids.values) {
            val handler = registry[ids.inverse()[ID]]?.client

            if (handler != null) {
                return handler(player, world, BlockPos(x, y, z))
            }
        }
        return null
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID in ids.values) {
            val handler = registry[ids.inverse()[ID]]?.server

            if (handler != null) {
                val handled = handler(player, world, BlockPos(x, y, z))
                if (handled != null)
                    return ContainerImpl(handled)
            }
        }
        return null
    }

}

private data class GuiEntry(
        val server: ((player: EntityPlayer, world: World, pos: BlockPos) -> ContainerBase?)?,
        val client: ((player: EntityPlayer, world: World, pos: BlockPos) -> GuiScreen?)?
)
