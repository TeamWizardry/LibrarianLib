package com.teamwizardry.librarianlib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Class provided by MCJty
 */
public class TeleportUtil {

	public static void teleportToDimension(EntityPlayer player, int dimension, double x, double y, double z) {
		int oldDimension = player.worldObj.provider.getDimension();
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
		MinecraftServer server = ((EntityPlayerMP) player).worldObj.getMinecraftServer();
		WorldServer worldServer = server.worldServerForDimension(dimension);
		player.addExperienceLevel(0);


		worldServer.getMinecraftServer().getPlayerList().transferPlayerToDimension(entityPlayerMP, dimension, new CustomTeleporter(worldServer, x, y, z));
		player.setPositionAndUpdate(x, y, z);
		if (oldDimension == 1) {
			// For some reason teleporting out of the end does weird things.
			player.setPositionAndUpdate(x, y, z);
			worldServer.spawnEntityInWorld(player);
			worldServer.updateEntityWithOptionalForce(player, false);
		}
	}


	public static class CustomTeleporter extends Teleporter {
		private final WorldServer worldServer;

		private double x;
		private double y;
		private double z;


		public CustomTeleporter(WorldServer world, double x, double y, double z) {
			super(world);
			this.worldServer = world;
			this.x = x;
			this.y = y;
			this.z = z;

		}

		@Override
		public void placeInPortal(Entity entity, float rotationYaw) {
			this.worldServer.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));

			entity.setPosition(this.x, this.y, this.z);
			entity.motionX = 0.0f;
			entity.motionY = 0.0f;
			entity.motionZ = 0.0f;
		}
	}
}
