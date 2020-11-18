package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.foundation.capability.SimpleCapabilityStorage
import com.teamwizardry.librarianlib.foundation.capability.SimpleCapabilityProvider
import com.teamwizardry.librarianlib.foundation.registration.CapabilitySpec
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestCapability
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModCapabilities {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    internal fun registerCapabilities(registrationManager: RegistrationManager) {
        registrationManager.add(CapabilitySpec(
            TestCapability::class.java,
            SimpleCapabilityStorage()
        ) {
            TestCapability()
        })
    }

    @SubscribeEvent
    fun attachTECapabilities(e: AttachCapabilitiesEvent<TileEntity>) {
        if(e.`object`.type == ModTiles.testTile.get()) {
            e.addCapability(
                loc(LibrarianLibFoundationTestMod.modid, "test_capability"),
                SimpleCapabilityProvider(TestCapability.capability, TestCapability())
            )
        }
    }
}