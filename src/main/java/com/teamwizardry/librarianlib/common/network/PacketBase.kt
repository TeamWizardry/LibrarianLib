package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.common.util.saving.ByteBufSerializationHandlers
import com.teamwizardry.librarianlib.common.util.saving.SavingFieldCache
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

abstract class PacketBase : IMessage {

    /**
     * Put your handling code for the message in here.
     * Assume all fields are already populated by the read methods.
     */
    abstract fun handle(ctx: MessageContext)

    /**
     * Override this to reply to any incoming messages with your own.
     * Leave the return null to reply with no message.
     * The resulting message is fired along the same channel as the incoming one.
     */
    open fun reply(ctx: MessageContext): PacketBase? = null

    /**
     * Override this to add custom write-to-bytes.
     * Make sure to have the same order for writing and reading.
     */
    open fun writeCustomBytes(buf: ByteBuf) {
        // NO-OP
    }

    /**
     * Override this to add custom read-from-bytes.
     * Make sure to have the same order for writing and reading.
     */
    open fun readCustomBytes(buf: ByteBuf) {
        // NO-OP
    }

    fun writeAutoBytes(buf: ByteBuf) {
        SavingFieldCache.getClassFields(javaClass).forEach {
            val handler = ByteBufSerializationHandlers.getWriterUnchecked(it.value.first)
            if (handler != null) {
                val field = it.value.second(this)
                if (field == null)
                    buf.writeBoolean(true)
                else {
                    buf.writeBoolean(false)
                    handler(buf, field)
                }
            } else
                buf.writeBoolean(true)
        }
    }

    fun readAutoBytes(buf: ByteBuf) {
        SavingFieldCache.getClassFields(javaClass).forEach {
            if (buf.readBoolean())
                it.value.third(this, null)
            else {
                val handler = ByteBufSerializationHandlers.getReaderUnchecked(it.value.first)
                if (handler != null) it.value.third(this, handler(buf))
            }
        }
    }

    override fun fromBytes(buf: ByteBuf) {
        readAutoBytes(buf)
        readCustomBytes(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        writeAutoBytes(buf)
        writeCustomBytes(buf)
    }
}
