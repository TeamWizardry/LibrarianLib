package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.kotlin.minus
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.FMLCommonHandler

interface PacketTarget {
    val players: List<EntityPlayer>
}

object TargetAll : PacketTarget {
    override val players: List<EntityPlayer>
        get() = FMLCommonHandler.instance().minecraftServerInstance.playerList.players
}

object TargetServer : PacketTarget {
    override val players: List<EntityPlayer>
        get() = throw UnsupportedOperationException("TargetServer target does not have any players")
}

data class TargetPlayers(override val players: List<EntityPlayer>) : PacketTarget {
    constructor(vararg players: EntityPlayer) : this(players.filter { it is EntityPlayer })
}

class TargetWorld(val world: World) : PacketTarget {
    override val players: List<EntityPlayer>
        get() = world.playerEntities
}

data class TargetRadius(val world: World, val pos: Vec3d, val radius: Int) : PacketTarget {
    override val players: List<EntityPlayer>
        get() {
            return world.playerEntities.filter {
                (it.positionVector - pos).lengthSquared() <= radius*radius
            }
        }
}

data class TargetWatchingBlock(val world: World, val pos: BlockPos) : PacketTarget {
    override val players: List<EntityPlayer>
        get() {
            if(world !is WorldServer) throw UnsupportedOperationException("Cannot target all watching block for non-server worlds")
            val chunkPos = ChunkPos(pos)
            val map = world.playerChunkMap
            return world.playerEntities.filter { player ->
                map.isPlayerWatchingChunk(player as EntityPlayerMP, chunkPos.x, chunkPos.z)
            }
        }
}

data class TargetWatchingEntity(val entity: Entity) : PacketTarget {
    override val players: List<EntityPlayer>
        get() {
            val world = entity.world
            if(world !is WorldServer) throw UnsupportedOperationException("Cannot target all watching block for non-server worlds")
            val chunkPos = ChunkPos(entity.position)
            val map = world.playerChunkMap
            return world.playerEntities.filter { player ->
                map.isPlayerWatchingChunk(player as EntityPlayerMP, chunkPos.x, chunkPos.z)
            }
        }
}
