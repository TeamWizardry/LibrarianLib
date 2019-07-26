package com.teamwizardry.librarianlib.core.client.commands

import com.teamwizardry.librarianlib.features.base.SimpleCommand
import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.hud.GuiHud
import com.teamwizardry.librarianlib.features.shader.ShaderHelper

object ShaderCommands {
    val root = SimpleCommandTree("shader", "librarianlib.command.liblib.shader.usage")

    init {
        root.addSubcommand(
            SimpleCommand("reload", "librarianlib.command.liblib.shader.reload.usage") { _, _, _ ->
                ShaderHelper.initShaders()
            }
        )

        val options = OptionCommand("options")
        root.addSubcommand(options)
        options.add(BooleanGuiOption("validateShaders", ShaderHelper::enableValidation))
    }
}


