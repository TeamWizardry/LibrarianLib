package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Created by TheCodeWarrior
 */
class PacketSyncSlotVisibility : PacketBase() {

    @Save
    var visibility = BooleanArray(0)

    override fun handle(ctx: MessageContext) {
        (ctx.serverHandler.playerEntity.openContainer as? ContainerImpl)?.container?.allSlots?.forEachIndexed { i, slot ->
            if(i < visibility.size)
                slot.visible = visibility[i]
        }
    }

}
