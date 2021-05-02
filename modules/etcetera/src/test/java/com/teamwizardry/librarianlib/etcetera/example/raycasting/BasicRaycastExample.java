package com.teamwizardry.librarianlib.etcetera.example.raycasting;
/*
import com.teamwizardry.librarianlib.etcetera.Raycaster;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

public class BasicRaycastExample {
    // note: Raycaster is *not* thread-safe, though world should only be
    // accessed from the main thread anyway.
    private static Raycaster raycaster = new Raycaster();

    @Nullable
    public BlockPos basicBlockRaycast(Entity entity) {
        Vector3d start = entity.getEyePosition(0);
        Vector3d look = entity.getLookVec();
        look = new Vector3d(
                look.getX() * 100,
                look.getY() * 100,
                look.getZ() * 100
        );

        // cast the ray
        raycaster.cast(entity.getEntityWorld(), Raycaster.BlockMode.VISUAL,
                start.getX(), start.getY(), start.getZ(),
                start.getX() + look.getX(),
                start.getY() + look.getY(),
                start.getZ() + look.getZ()
        );

        // get the result out of it
        BlockPos result = null;
        if (raycaster.getHitType() == Raycaster.HitType.BLOCK) {
            result = new BlockPos(
                    raycaster.getBlockX(),
                    raycaster.getBlockY(),
                    raycaster.getBlockZ()
            );
        }

        // it is VITALLY important that you do this
        raycaster.reset();
        return result;
    }
}


 */