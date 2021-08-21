package com.teamwizardry.librarianlib.testcore.mixin;

import com.teamwizardry.librarianlib.testcore.bridge.TestCoreEntityTypes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class EntitySpawningMixin {
    @Shadow private ClientWorld world;

    @Inject(method = "onEntitySpawn", at = @At("RETURN"))
    public void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if(TestCoreEntityTypes.INSTANCE.getTypes().contains(packet.getEntityTypeId())) {
            Entity entity = packet.getEntityTypeId().create(world);
            if(entity != null) {
                double x = packet.getX();
                double y = packet.getY();
                double z = packet.getZ();
                int i = packet.getId();
                entity.updateTrackedPosition(x, y, z);
                entity.refreshPositionAfterTeleport(x, y, z);
                entity.setPitch((float)(packet.getPitch() * 360) / 256.0F);
                entity.setYaw((float)(packet.getYaw() * 360) / 256.0F);
                entity.setId(i);
                entity.setUuid(packet.getUuid());
                this.world.addEntity(i, entity);
            }
        }
    }
}
