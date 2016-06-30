package com.teamwizardry.librarianlib.api.util.misc;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/29/2016.
 */
public class PosUtils {

    public static BlockPos adjustPositionToBlock(World world, BlockPos origin, Block desiredBlockToFind) {

        // Check all directions on the same level of the origin
        if (world.getBlockState(origin).getBlock() == desiredBlockToFind) return origin;
        else if (world.getBlockState(origin.down()).getBlock() == desiredBlockToFind)
            return origin.down();
        else if (world.getBlockState(origin.east()).getBlock() == desiredBlockToFind)
            return origin.east();
        else if (world.getBlockState(origin.west()).getBlock() == desiredBlockToFind)
            return origin.west();
        else if (world.getBlockState(origin.north()).getBlock() == desiredBlockToFind)
            return origin.north();
        else if (world.getBlockState(origin.south()).getBlock() == desiredBlockToFind)
            return origin.south();
        else if (world.getBlockState(origin.north().west()).getBlock() == desiredBlockToFind)
            return origin.north().west();
        else if (world.getBlockState(origin.north().east()).getBlock() == desiredBlockToFind)
            return origin.north().east();
        else if (world.getBlockState(origin.south().west()).getBlock() == desiredBlockToFind)
            return origin.south().west();
        else if (world.getBlockState(origin.south().east()).getBlock() == desiredBlockToFind)
            return origin.south().east();

            // Check all directions UNDER the origin
        else if (world.getBlockState(origin.east().down()).getBlock() == desiredBlockToFind)
            return origin.east().down();
        else if (world.getBlockState(origin.west().down()).getBlock() == desiredBlockToFind)
            return origin.west().down();
        else if (world.getBlockState(origin.north().down()).getBlock() == desiredBlockToFind)
            return origin.north().down();
        else if (world.getBlockState(origin.south().down()).getBlock() == desiredBlockToFind)
            return origin.south().down();
        else if (world.getBlockState(origin.north().west().down()).getBlock() == desiredBlockToFind)
            return origin.north().west().down();
        else if (world.getBlockState(origin.north().east().down()).getBlock() == desiredBlockToFind)
            return origin.north().east().down();
        else if (world.getBlockState(origin.south().west().down()).getBlock() == desiredBlockToFind)
            return origin.south().west().down();
        else if (world.getBlockState(origin.south().east().down()).getBlock() == desiredBlockToFind)
            return origin.south().east().down();

        else return origin;
    }

    public static Vec3d generateRandomPosition(Vec3d origin, double range) {
        double x = origin.xCoord;
        double y = origin.yCoord;
        double z = origin.zCoord;
        x = ThreadLocalRandom.current().nextDouble(x - range, x + range);
        y = ThreadLocalRandom.current().nextDouble(y - range, y + range);
        z = ThreadLocalRandom.current().nextDouble(z - range, z + range);
        return new Vec3d(x, y, z);
    }
}
