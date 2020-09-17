package com.teamwizardry.librarianlib.courier

import dev.thecodewarrior.prism.annotation.Refract
import net.minecraft.network.PacketBuffer
import net.minecraft.world.IWorldReader
import net.minecraftforge.fml.network.NetworkEvent

public interface CourierPacket {
    /**
     * Writes the packet data to the buffer. Any [@Refract][Refract] annotated fields do NOT need to be written in this
     * method.
     */
    public fun writeBytes(buffer: PacketBuffer) {}

    /**
     * Reads the packet data from the buffer. Any [@Refract][Refract] annotated fields do NOT need to be read in this
     * method.
     */
    public fun readBytes(buffer: PacketBuffer) {}

    /**
     * Handles the packet being received. This is handled on the network thread, so don't access anything that isn't
     * thread-safe (i.e. most Minecraft stuff). Call [context].[enqueueWork(Runnable)][NetworkEvent.Context.enqueueWork]
     * to run something on the main thread.
     *
     * NOTE! Write this code defensively. For example, if you're receiving a block position from the client, make sure
     * that block is actually loaded ([`world.isBlockLoaded`][IWorldReader.isBlockLoaded]) before doing anything,
     * otherwise you might allow a maliciously modified client to generate arbitrary chunks. If you're receiving a value
     * from 0–1 from the client (e.g. jump force), *make sure it's really 0–1*, otherwise someone could hack their
     * client to send a jump height of 5 and be able to jump way higher than they should.
     *
     * In short, ***NEVER*** trust the client.
     */
    public fun handle(context: NetworkEvent.Context) {}
}