package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText

class SimpleContainer(windowId: Int, player: PlayerEntity): FacadeContainer(LibrarianLibFacadeTestMod.simpleContainerType, windowId, player) {
    override fun canUse(playerIn: PlayerEntity): Boolean {
        return true
    }

    @Message
    private fun buttonClick(position: Vec2d) {
        val prefix = if(isClientContainer) "[Client Container]" else "[Server Container]"
        player.sendMessage(LiteralText("$prefix button click: $position"), false)
    }
}