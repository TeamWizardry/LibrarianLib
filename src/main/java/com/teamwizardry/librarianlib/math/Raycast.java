package com.teamwizardry.librarianlib.math;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.List;

public class Raycast
{
	/**
	 * Gets the first block or entity along the given line from the given entity
	 * 
	 * @param entity
	 *            The entity from which to search
	 * @param dir
	 *            The direction along which to search
	 * @param distance
	 *            The distance to check
	 * @return The first block or entity along the given ray
	 */
	public static RayTraceResult cast(Entity entity, Vec3d dir, double distance)
	{
		if (entity != null)
		{
			RayTraceResult focusedBlock = blockTrace(entity, distance);
			Vec3d eyePos = entity.getPositionEyes(1);
			double blockDistance = distance;

			if (focusedBlock != null)
				blockDistance = focusedBlock.hitVec.distanceTo(eyePos);

			Vec3d cast = eyePos.addVector(dir.xCoord*distance, dir.yCoord*distance, dir.zCoord*distance);
			Entity focusedEntity = null;
			Vec3d vec = null;
			List<Entity> list = entity.worldObj.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance).expand(1, 1, 1), Predicates.and(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity apply) {
					return apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem);
				}
			}, EntitySelectors.NOT_SPECTATING));
			double blockDistCopy = blockDistance;

			int j = 0;
			while (j < list.size()) {
				Entity current = list.get(j);
				AxisAlignedBB axis = current.getEntityBoundingBox().expandXyz(current.getCollisionBorderSize());
				RayTraceResult result = axis.calculateIntercept(eyePos, cast);

				if (axis.isVecInside(eyePos))
				{
					if (blockDistCopy > 0)
					{
						focusedEntity = current;
						vec = result == null ? eyePos : result.hitVec;
						blockDistCopy = 0;
					}
				}
				else if (result != null)
				{
					double entityDistance = eyePos.distanceTo(result.hitVec);

					if (entityDistance < blockDistCopy || blockDistCopy == 0)
					{
						if (current.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity.canRiderInteract())
						{
							if (blockDistCopy == 0)
							{
								focusedEntity = current;
								vec = result.hitVec;
							}
						}
						else
						{
							focusedEntity = current;
							vec = result.hitVec;
							blockDistCopy = entityDistance;
						}
					}
				}

				if (focusedEntity != null && (blockDistCopy < blockDistance || focusedBlock == null))
				{
					focusedBlock = new RayTraceResult(focusedEntity, vec);
					if (focusedEntity instanceof EntityLivingBase || focusedEntity instanceof EntityItem) { return focusedBlock; }
				}
				j++;
			}
			return focusedBlock;
		}
		return null;
	}

	/**
	 * Gets the first block or entity along the given entity's look vector
	 * 
	 * @param entity
	 *            The entity being cast from
	 * @param distance
	 *            The distance to cast the vector
	 * @return The first block or entity colliding with the vector
	 */
	public static RayTraceResult cast(Entity entity, double distance)
	{
		return cast(entity, entity.getLook(1), distance);
	}

	private static RayTraceResult blockTrace(Entity entity, double distance)
	{
		return blockTrace(entity, entity.getLook(1), distance);
	}

	private static RayTraceResult blockTrace(Entity entity, Vec3d ray, double distance)
	{
		Vec3d pos = entity.getPositionEyes(1);
		Vec3d cast = pos.add(ray.scale(distance));
		return entity.worldObj.rayTraceBlocks(pos, cast, false, false, true);
	}
}
