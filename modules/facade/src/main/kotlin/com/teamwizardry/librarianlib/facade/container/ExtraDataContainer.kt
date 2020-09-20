package com.teamwizardry.librarianlib.facade.container

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.network.NetworkHooks

public interface ExtraDataContainer {
    public fun writeExtraData(buffer: PacketBuffer)
    public fun readExtraData(buffer: PacketBuffer)

    public companion object {
        /**
         * Opens a GUI container. This is similar to [NetworkHooks.openGui] with the difference that any container
         * instance implementing [ExtraDataContainer] will be given the opportunity to write its own data into the
         * outgoing packet.
         */
        @JvmStatic
        public fun open(player: ServerPlayerEntity, provider: INamedContainerProvider) {
            val oldId = player.currentWindowId
            player.getNextWindowId()
            val windowId = player.currentWindowId
            val container = provider.createMenu(windowId, player.inventory, player)
            player.currentWindowId = oldId

            NetworkHooks.openGui(player, object: INamedContainerProvider {
                override fun createMenu(p_createMenu_1_: Int, p_createMenu_2_: PlayerInventory, p_createMenu_3_: PlayerEntity): Container? {
                    if (p_createMenu_1_ != windowId) {
                        throw IllegalStateException("Predicted window id $windowId was incorrect. Actual window id was $p_createMenu_1_")
                    }
                    return container
                }

                override fun getDisplayName(): ITextComponent {
                    return provider.displayName
                }
            }) { buffer ->
                (container as? ExtraDataContainer)?.writeExtraData(buffer)
            }
        }
    }
}
