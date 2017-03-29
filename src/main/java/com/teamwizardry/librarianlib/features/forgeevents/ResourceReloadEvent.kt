package com.teamwizardry.librarianlib.features.forgeevents

import net.minecraft.client.resources.IResourceManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 8:00 PM on 11/10/16.
 *
 * Fired when resources are reloaded.
 * Fired on the [MinecraftForge.EVENT_BUS], on the client only.
 */
@SideOnly(Side.CLIENT)
class ResourceReloadEvent(val reloadManager: IResourceManager) : Event()
