package com.teamwizardry.librarianlib.client.core

import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.util.math.Vec3d

/**
 * Created by TheCodeWarrior
 */
fun VertexBuffer.pos(pos: Vec3d): VertexBuffer = this.pos(pos.xCoord, pos.yCoord, pos.zCoord)
