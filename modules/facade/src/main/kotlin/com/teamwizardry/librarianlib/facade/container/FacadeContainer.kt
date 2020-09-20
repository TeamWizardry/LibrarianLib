package com.teamwizardry.librarianlib.facade.container

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.network.NetworkHooks
import java.lang.IllegalStateException

public abstract class FacadeContainer(type: ContainerType<*>?, id: Int): Container(type, id) {
//    @get:JvmSynthetic
//    internal val messager = ContainerMessager(this, windowId, )
}
