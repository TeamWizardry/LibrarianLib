package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.registry.RegistryKeys


public object TestCoreMod {
    public const val MODID: String = "liblib_testcore"
    public val logManager: ModLogManager = ModLogManager(MODID, "LibrarianLib: Test Core")

    public val registrarManager: RegistrarManager by lazy { RegistrarManager.get(MODID) }
    public val argumentTypeRegistrar: Registrar<ArgumentSerializer<*, *>> by lazy { registrarManager.get(RegistryKeys.COMMAND_ARGUMENT_TYPE) }
}
