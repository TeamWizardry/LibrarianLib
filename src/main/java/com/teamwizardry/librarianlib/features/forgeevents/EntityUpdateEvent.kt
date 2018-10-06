package com.teamwizardry.librarianlib.features.forgeevents

import net.minecraft.entity.Entity
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fml.common.eventhandler.Cancelable

/**
 * @author WireSegal
 * Created at 1:37 PM on 3/3/18.
 */
@Cancelable
class EntityUpdateEvent(entity: Entity) : EntityEvent(entity)

class EntityPostUpdateEvent(entity: Entity) : EntityEvent(entity)
