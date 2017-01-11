package com.teamwizardry.librarianlib.common.container

import com.google.common.collect.HashBiMap
import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.util.getTileEntitySafely
import net.minecraft.entity.player.EntityPlayer
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

    @JvmStatic
    @JvmOverloads
    fun open(name: ResourceLocation, player: EntityPlayer, pos: BlockPos = BlockPos.ORIGIN) {
        if(name !in ids)
            throw IllegalArgumentException("No GUI handler registered for $name")
        if(!player.world.isRemote) {
            player.openGui(LibrarianLib.INSTANCE, ids[name]!!, player.world, pos.x, pos.y, pos.z)
        }
    }

    @JvmStatic
    fun registerRaw(name: ResourceLocation,
                    server: ((player: EntityPlayer, world: World, pos: BlockPos) -> ContainerBase)?,
                    client: ((player: EntityPlayer, world: World, pos: BlockPos) -> GuiContainerBase)?) {
        if (name !in ids.keys) {
            ids.put(name, ids.size)
        }
        if (name in registry) {
            throw IllegalArgumentException("GUI handler for $name already exists")
        }
        registry.put(name, GuiEntry(server, client))
    }

    @JvmStatic
    fun <T : ContainerBase> registerBasicContainer(name: ResourceLocation,
                                                   server: (player: EntityPlayer, pos: BlockPos, te: TileEntity?) -> T,
                                                   client: (player: EntityPlayer, container: T) -> GuiContainerBase) {
        val rawServer: (EntityPlayer, World, BlockPos) -> T = { player, world, pos ->
            server(player, pos, world.getTileEntitySafely(pos))
        }

        val rawClient: (EntityPlayer, World, BlockPos) -> GuiContainerBase = { player, world, pos ->
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
                return ContainerImpl(handler(player, world, BlockPos(x, y, z)))
            }
        }
        return null
    }

}

private data class GuiEntry(
        val server: ((player: EntityPlayer, world: World, pos: BlockPos) -> ContainerBase)?,
        val client: ((player: EntityPlayer, world: World, pos: BlockPos) -> GuiContainerBase)?
)
