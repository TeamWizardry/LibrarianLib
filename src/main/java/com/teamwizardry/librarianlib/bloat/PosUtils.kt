package com.teamwizardry.librarianlib.bloat

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Saad on 6/29/2016.
 */
object PosUtils {

    fun adjustPositionToBlock(world: World, origin: BlockPos, desiredBlockToFind: Block): BlockPos {

        // Check all directions on the same level of the origin
        if (world.getBlockState(origin).block === desiredBlockToFind)
            return origin
        else if (world.getBlockState(origin.down()).block === desiredBlockToFind)
            return origin.down()
        else if (world.getBlockState(origin.east()).block === desiredBlockToFind)
            return origin.east()
        else if (world.getBlockState(origin.west()).block === desiredBlockToFind)
            return origin.west()
        else if (world.getBlockState(origin.north()).block === desiredBlockToFind)
            return origin.north()
        else if (world.getBlockState(origin.south()).block === desiredBlockToFind)
            return origin.south()
        else if (world.getBlockState(origin.north().west()).block === desiredBlockToFind)
            return origin.north().west()
        else if (world.getBlockState(origin.north().east()).block === desiredBlockToFind)
            return origin.north().east()
        else if (world.getBlockState(origin.south().west()).block === desiredBlockToFind)
            return origin.south().west()
        else if (world.getBlockState(origin.south().east()).block === desiredBlockToFind)
            return origin.south().east()
        else if (world.getBlockState(origin.east().down()).block === desiredBlockToFind)
            return origin.east().down()
        else if (world.getBlockState(origin.west().down()).block === desiredBlockToFind)
            return origin.west().down()
        else if (world.getBlockState(origin.north().down()).block === desiredBlockToFind)
            return origin.north().down()
        else if (world.getBlockState(origin.south().down()).block === desiredBlockToFind)
            return origin.south().down()
        else if (world.getBlockState(origin.north().west().down()).block === desiredBlockToFind)
            return origin.north().west().down()
        else if (world.getBlockState(origin.north().east().down()).block === desiredBlockToFind)
            return origin.north().east().down()
        else if (world.getBlockState(origin.south().west().down()).block === desiredBlockToFind)
            return origin.south().west().down()
        else if (world.getBlockState(origin.south().east().down()).block === desiredBlockToFind)
            return origin.south().east().down()
        else
            return origin// Check all directions UNDER the origin
    }

    fun generateRandomPosition(origin: Vec3d, range: Double): Vec3d {
        var x = origin.xCoord
        var y = origin.yCoord
        var z = origin.zCoord
        x = ThreadLocalRandom.current().nextDouble(x - range, x + range)
        y = ThreadLocalRandom.current().nextDouble(y - range, y + range)
        z = ThreadLocalRandom.current().nextDouble(z - range, z + range)
        return Vec3d(x, y, z)
    }

    fun hasNeighboringBlock(world: World, origin: BlockPos, check: Block, checkDiagonals: Boolean, checkVertically: Boolean): Boolean {
        if (world.getBlockState(origin.south()).block === check)
            return true
        else if (world.getBlockState(origin.north()).block === check)
            return true
        else if (world.getBlockState(origin.east()).block === check)
            return true
        else if (world.getBlockState(origin.west()).block === check)
            return true
        else if (checkVertically) {

            if (world.getBlockState(origin.down()).block === check)
                return true
            else if (world.getBlockState(origin.up()).block === check)
                return true
            else if (checkDiagonals) {

                if (world.getBlockState(origin.down().south()).block === check)
                    return true
                else if (world.getBlockState(origin.down().north()).block === check)
                    return true
                else if (world.getBlockState(origin.down().east()).block === check)
                    return true
                else if (world.getBlockState(origin.down().west()).block === check) return true

                if (world.getBlockState(origin.up().south()).block === check)
                    return true
                else if (world.getBlockState(origin.up().north()).block === check)
                    return true
                else if (world.getBlockState(origin.up().east()).block === check)
                    return true
                else if (world.getBlockState(origin.up().west()).block === check) return true
            }
        }

        if (checkDiagonals) {
            if (world.getBlockState(origin.south().west()).block === check)
                return true
            else if (world.getBlockState(origin.north().west()).block === check)
                return true
            else if (world.getBlockState(origin.south().east()).block === check)
                return true
            else if (world.getBlockState(origin.north().east()).block === check) return true

        }
        return false
    }
}
