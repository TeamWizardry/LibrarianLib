package com.teamwizardry.librarianlib.features.network

import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper

/**
 * Created by TheCodeWarrior
 */

fun SimpleNetworkWrapper.sendToAllAround(packet: IMessage, world: World, pos: Vec3d, radius: Number) {
    this.sendToAllAround(packet, NetworkRegistry.TargetPoint(world.provider.dimension, pos.xCoord, pos.yCoord, pos.zCoord, radius.toDouble()))
}
