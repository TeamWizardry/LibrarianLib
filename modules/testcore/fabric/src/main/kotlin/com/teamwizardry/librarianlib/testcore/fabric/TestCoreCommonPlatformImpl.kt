package com.teamwizardry.librarianlib.testcore.fabric

import com.google.auto.service.AutoService
import com.mojang.brigadier.arguments.ArgumentType
import com.teamwizardry.librarianlib.testcore.platform.TestCoreCommonPlatform
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.util.Identifier

@AutoService(TestCoreCommonPlatform::class)
internal class TestCoreCommonPlatformImpl : TestCoreCommonPlatform {
    override fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerArgumentType(
        id: Identifier,
        clazz: Class<A>,
        serializer: ArgumentSerializer<A, T>
    ) = ArgumentTypeRegistry.registerArgumentType(id, clazz, serializer)
}