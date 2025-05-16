package com.teamwizardry.librarianlib.testcore.platform

import com.mojang.brigadier.arguments.ArgumentType
import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.util.Identifier

public interface TestCoreCommonPlatform {
    public fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerArgumentType(
        id: Identifier,
        clazz: Class<A>,
        serializer: ArgumentSerializer<A, T>
    )

    public companion object {
        public val instance: TestCoreCommonPlatform by ServiceLoaderHelper.required()
    }
}