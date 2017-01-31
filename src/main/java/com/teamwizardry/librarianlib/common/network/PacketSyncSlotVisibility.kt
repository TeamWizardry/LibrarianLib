package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by TheCodeWarrior
 */
@PacketRegister(Side.SERVER)
class PacketSyncSlotVisibility(@Save var visibility: BooleanArray = BooleanArray(0)) : PacketBase() {

    override fun handle(ctx: MessageContext) {
        (ctx.serverHandler.playerEntity.openContainer as? ContainerImpl)?.container?.allSlots?.forEachIndexed { i, slot ->
            if(i < visibility.size)
                slot.visible = visibility[i]
        }
    }

}
