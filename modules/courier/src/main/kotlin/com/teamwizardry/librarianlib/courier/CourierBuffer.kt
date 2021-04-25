package com.teamwizardry.librarianlib.courier

import com.google.common.base.Preconditions
import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import kotlin.Throws
import java.io.IOException
import net.minecraft.nbt.CompoundNBT
import kotlin.jvm.JvmOverloads
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.SectionPos
import net.minecraft.util.text.ITextComponent
import java.lang.RuntimeException
import net.minecraft.nbt.CompressedStreamTools
import io.netty.buffer.ByteBufOutputStream
import net.minecraft.nbt.NBTSizeTracker
import io.netty.buffer.ByteBufInputStream
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.vector.Vector3d
import io.netty.buffer.ByteBufAllocator
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import io.netty.util.ByteProcessor
import net.minecraft.item.Item
import net.minecraft.network.PacketBuffer
import net.minecraft.util.Direction
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryManager
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.GatheringByteChannel
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.StandardCharsets
import java.util.*

public class CourierBuffer(public val buf: PacketBuffer) : ByteBuf() {
    @Throws(IOException::class)
    public fun <T> decode(codec: Codec<T>): T = buf.func_240628_a_(codec)

    @Throws(IOException::class)
    public fun <T> encode(codec: Codec<T>, value: T): CourierBuffer = build { buf.func_240629_a_(codec, value) }

    public fun writeByteArray(array: ByteArray): CourierBuffer = build { buf.writeByteArray(array) }

    @JvmOverloads
    public fun readByteArray(maxLength: Int = readableBytes()): ByteArray = buf.readByteArray(maxLength)

    /**
     * Writes an array of VarInts to the buffer, prefixed by the length of the array (as a VarInt).
     */
    public fun writeVarIntArray(array: IntArray): CourierBuffer = build { buf.writeVarIntArray(array) }

    @JvmOverloads
    public fun readVarIntArray(maxLength: Int = readableBytes()): IntArray = buf.readVarIntArray(maxLength)

    /**
     * Writes an array of longs to the buffer, prefixed by the length of the array (as a VarInt).
     */
    public fun writeLongArray(array: LongArray): CourierBuffer {
        writeVarInt(array.size)
        for (i in array) {
            writeLong(i)
        }
        return this
    }

    //@OnlyIn(Dist.CLIENT)
    /**
     * Reads a length-prefixed array of longs from the buffer.
     */
    @JvmOverloads
    public fun readLongArray(existingArray: LongArray?, maxLength: Int = readableBytes() / 8): LongArray {
        var array = existingArray
        val i = buf.readVarInt()
        if (array == null || array.size != i) {
            if (i > maxLength) {
                throw DecoderException("LongArray with size $i is bigger than allowed $maxLength")
            }
            array = LongArray(i)
        }
        for (j in array.indices) {
            array[j] = readLong()
        }
        return array
    }

    public fun readBlockPos(): BlockPos = buf.readBlockPos()
    public fun writeBlockPos(pos: BlockPos): CourierBuffer = build { buf.writeBlockPos(pos) }

    //@OnlyIn(Dist.CLIENT)
    public fun readSectionPos(): SectionPos {
        return SectionPos.from(readLong())
    }

    public fun readTextComponent(): ITextComponent? = buf.readTextComponent()
    public fun writeTextComponent(component: ITextComponent): CourierBuffer =
        build { buf.writeTextComponent(component) }

    public fun <T : Enum<T>> readEnumValue(enumClass: Class<T>): T = buf.readEnumValue(enumClass)

    @JvmSynthetic
    public inline fun <reified T : Enum<T>> readEnumValue(): T = buf.readEnumValue(T::class.java) as T
    public fun writeEnumValue(value: Enum<*>): CourierBuffer = build { buf.writeEnumValue(value) }

    /**
     * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant bit
     * dictates whether another byte should be read.
     */
    public fun readVarInt(): Int = buf.readVarInt()
    public fun readVarLong(): Long = buf.readVarLong()

    public fun writeUniqueId(uuid: UUID): CourierBuffer = build { buf.writeUniqueId(uuid) }
    public fun readUniqueId(): UUID = buf.readUniqueId()

    /**
     * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of each
     * such byte only 7 bits will be used to describe the actual value since its most significant bit dictates whether
     * the next byte is part of that same int. Micro-optimization for int values that are expected to have values
     * below 128.
     */
    public fun writeVarInt(input: Int): CourierBuffer = build { buf.writeVarInt(input) }
    public fun writeVarLong(value: Long): CourierBuffer = build { buf.writeVarLong(value) }

    /**
     * Writes a compressed NBTTagCompound to this buffer
     */
    public fun writeCompoundTag(nbt: CompoundNBT?): CourierBuffer = build { buf.writeCompoundTag(nbt) }
    /**
     * Reads a compressed NBTTagCompound from this buffer
     */
    public fun readCompoundTag(): CompoundNBT? = buf.readCompoundTag()
    public fun readUnlimitedSizeCompoundTag(): CompoundNBT? = buf.func_244273_m()
    public fun readCompoundTag(sizeTracker: NBTSizeTracker): CompoundNBT? = buf.func_244272_a(sizeTracker)

    /**
     * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
     *
     * Most ItemStack serialization is Server to Client,and doesn't need to know the FULL tag details.
     * One exception is items from the creative menu, which must be sent from Client to Server with their full NBT.
     * If you want to send the FULL tag set limitedTag to false
     */
    @JvmOverloads
    public fun writeItemStack(stack: ItemStack, limitedTag: Boolean = true): CourierBuffer =
        build { buf.writeItemStack(stack, limitedTag) }
    /**
     * Reads an ItemStack from this buffer
     */
    public fun readItemStack(): ItemStack = buf.readItemStack()

    //@OnlyIn(Dist.CLIENT)
    /**
     * Reads a string from this buffer. Expected parameter is maximum allowed string length. Will throw IOException if
     * string length exceeds this value!
     */
    @JvmOverloads
    public fun readString(maxLength: Int = 32767): String {
        val i = readVarInt()
        if (i > maxLength * 4) {
            throw DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")")
        } else if (i < 0) {
            throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")
        } else {
            val s = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8)
            this.readerIndex(this.readerIndex() + i)
            if (s.length > maxLength) {
                throw DecoderException("The received string length is longer than maximum allowed ($i > $maxLength)")
            }
            return s
        }
    }

    @JvmOverloads
    public fun writeString(string: String, maxLength: Int = 32767): CourierBuffer {
        val abyte = string.toByteArray(StandardCharsets.UTF_8)
        return if (abyte.size > maxLength) {
            throw EncoderException("String too big (was " + abyte.size + " bytes encoded, max " + maxLength + ")")
        } else {
            writeVarInt(abyte.size)
            this.writeBytes(abyte)
            this
        }
    }

    public fun readIdentifier(): Identifier = buf.readIdentifier()
    public fun writeIdentifier(IdentifierIn: Identifier): CourierBuffer =
        build { buf.writeIdentifier(IdentifierIn) }

    public fun readTime(): Date = buf.readTime()
    public fun writeTime(time: Date): CourierBuffer = build { buf.writeTime(time) }

    public fun readBlockRay(): BlockRayTraceResult = buf.readBlockRay()
    public fun writeBlockRay(resultIn: BlockRayTraceResult): CourierBuffer = build { buf.writeBlockRay(resultIn) }

    //region forge

    /**
     * Writes the given entries integer id to the buffer. Notice however that this will only write the id of the given entry and will not check whether it actually exists
     * in the given registry. Therefore no safety checks can be performed whilst reading it and if the entry is not in the registry a default value will be written.
     * @param registry The registry containing the given entry
     * @param entry The entry who's registryName is to be written
     * @param <T> The type of the entry.
     */
    public fun <T : IForgeRegistryEntry<T>> writeRegistryIdUnsafe(
        registry: IForgeRegistry<T>,
        entry: T
    ): CourierBuffer = build { buf.writeRegistryIdUnsafe(registry, entry) }

    /**
     * Writes the given entries integer id to the buffer. Notice however that this will only write the id of the given entry and will not check whether it actually exists
     * in the given registry. Therefore no safety checks can be performed whilst reading it and if the entry is not in the registry a default value will be written.
     * @param registry The registry containing the entry represented by this key
     * @param entryKey The registry-name of an entry in this [IForgeRegistry]
     */
    public fun writeRegistryIdUnsafe(registry: IForgeRegistry<*>, entryKey: Identifier): CourierBuffer =
        build { buf.writeRegistryIdUnsafe(registry, entryKey) }

    /**
     * Reads an integer value from the buffer, which will be interpreted as an registry-id in the given registry. Notice that if there is no value in the specified registry for the
     * read id, that the registry's default value will be returned.
     * @param registry The registry containing the entry
     */
    public fun <T : IForgeRegistryEntry<T>> readRegistryIdUnsafe(registry: IForgeRegistry<T>): T =
        buf.readRegistryIdUnsafe(registry)

    /**
     * Writes an given registry-entry's integer id to the specified buffer in combination with writing the containing registry's id. In contrast to
     * [.writeRegistryIdUnsafe] this method checks every single step performed as well as
     * writing the registry-id to the buffer, in order to prevent any unexpected behaviour. Therefore this method is to be preferred whenever possible,
     * over using the unsafe methods.
     * @param entry The entry to write
     * @param <T> The type of the registry-entry
     * @throws NullPointerException if the entry was null
     * @throws IllegalArgumentException if the the registry could not be found or the registry does not contain the specified value
     */
    public fun <T : IForgeRegistryEntry<T>> writeRegistryId(entry: T): CourierBuffer =
        build { buf.writeRegistryId(entry) }

    /**
     * Reads an registry-entry from the specified buffer. Notice however that the type cannot be checked without providing an additional class parameter
     * - see [.readRegistryIdSafe] for an safe version.
     * @param <T> The type of the registry-entry. Notice that this should match the actual type written to the buffer.
     * @throws NullPointerException if the registry could not be found.
     */
    public fun <T : IForgeRegistryEntry<T>> readRegistryId(): T = buf.readRegistryId()

    /**
     * Reads an registry-entry from the specified buffer. This method also verifies, that the value read is of the appropriate type.
     * @param <T> The type of the registry-entry.
     * @throws IllegalArgumentException if the retrieved entries registryType doesn't match the one passed in.
     * @throws NullPointerException if the registry could not be found.
     */
    public fun <T : IForgeRegistryEntry<T>> readRegistryIdSafe(registrySuperType: Class<in T>): T =
        buf.readRegistryIdSafe(registrySuperType)

    /**
     * Writes a FluidStack to the packet buffer, easy enough. If EMPTY, writes a FALSE.
     * This behavior provides parity with the ItemStack method in PacketBuffer.
     *
     * @param stack FluidStack to be written to the packet buffer.
     */
    public fun writeFluidStack(stack: FluidStack): CourierBuffer = build { buf.writeFluidStack(stack) }
    /**
     * Reads a FluidStack from this buffer.
     */
    public fun readFluidStack(): FluidStack = buf.readFluidStack()

    //endregion forge

    override fun capacity(): Int = buf.capacity()
    override fun capacity(newCapacity: Int): ByteBuf = buf.capacity(newCapacity)
    override fun maxCapacity(): Int = buf.maxCapacity()

    override fun alloc(): ByteBufAllocator = buf.alloc()

    override fun order(): ByteOrder = buf.order()
    override fun order(endianness: ByteOrder): ByteBuf = buf.order(endianness)

    override fun unwrap(): ByteBuf = buf.unwrap()
    override fun isDirect(): Boolean = buf.isDirect
    override fun isReadOnly(): Boolean = buf.isReadOnly
    override fun asReadOnly(): ByteBuf = buf.asReadOnly()

    override fun readerIndex(): Int = buf.readerIndex()
    override fun readerIndex(readerIndex: Int): ByteBuf = buf.readerIndex(readerIndex)
    override fun writerIndex(): Int = buf.writerIndex()
    override fun writerIndex(writerIndex: Int): ByteBuf = buf.writerIndex(writerIndex)
    override fun setIndex(readerIndex: Int, writerIndex: Int): ByteBuf = buf.setIndex(readerIndex, writerIndex)

    override fun readableBytes(): Int = buf.readableBytes()
    override fun writableBytes(): Int = buf.writableBytes()
    override fun maxWritableBytes(): Int = buf.maxWritableBytes()

    override fun isReadable(): Boolean = buf.isReadable
    override fun isReadable(size: Int): Boolean = buf.isReadable(size)
    override fun isWritable(): Boolean = buf.isWritable
    override fun isWritable(size: Int): Boolean = buf.isWritable(size)

    override fun clear(): ByteBuf = buf.clear()

    override fun markReaderIndex(): ByteBuf = buf.markReaderIndex()
    override fun resetReaderIndex(): ByteBuf = buf.resetReaderIndex()
    override fun markWriterIndex(): ByteBuf = buf.markWriterIndex()
    override fun resetWriterIndex(): ByteBuf = buf.resetWriterIndex()

    override fun discardReadBytes(): ByteBuf = buf.discardReadBytes()
    override fun discardSomeReadBytes(): ByteBuf = buf.discardSomeReadBytes()

    override fun ensureWritable(minWritableBytes: Int): ByteBuf = buf.ensureWritable(minWritableBytes)
    override fun ensureWritable(minWritableBytes: Int, force: Boolean): Int =
        buf.ensureWritable(minWritableBytes, force)

    override fun getBoolean(index: Int): Boolean = buf.getBoolean(index)
    override fun getByte(index: Int): Byte = buf.getByte(index)
    override fun getUnsignedByte(index: Int): Short = buf.getUnsignedByte(index)
    override fun getShort(index: Int): Short = buf.getShort(index)
    override fun getShortLE(index: Int): Short = buf.getShortLE(index)
    override fun getUnsignedShort(index: Int): Int = buf.getUnsignedShort(index)
    override fun getUnsignedShortLE(index: Int): Int = buf.getUnsignedShortLE(index)
    override fun getMedium(index: Int): Int = buf.getMedium(index)
    override fun getMediumLE(index: Int): Int = buf.getMediumLE(index)
    override fun getUnsignedMedium(index: Int): Int = buf.getUnsignedMedium(index)
    override fun getUnsignedMediumLE(index: Int): Int = buf.getUnsignedMediumLE(index)
    override fun getInt(index: Int): Int = buf.getInt(index)
    override fun getIntLE(index: Int): Int = buf.getIntLE(index)
    override fun getUnsignedInt(index: Int): Long = buf.getUnsignedInt(index)
    override fun getUnsignedIntLE(index: Int): Long = buf.getUnsignedIntLE(index)
    override fun getLong(index: Int): Long = buf.getLong(index)
    override fun getLongLE(index: Int): Long = buf.getLongLE(index)
    override fun getChar(index: Int): Char = buf.getChar(index)
    override fun getFloat(index: Int): Float = buf.getFloat(index)
    override fun getDouble(index: Int): Double = buf.getDouble(index)
    override fun getBytes(index: Int, length: ByteBuf): ByteBuf = buf.getBytes(index, length)
    override fun getBytes(index: Int, dst: ByteBuf, length: Int): ByteBuf = buf.getBytes(index, dst, length)
    override fun getBytes(index: Int, dst: ByteBuf, dstIndex: Int, length: Int): ByteBuf =
        buf.getBytes(index, dst, dstIndex, length)

    override fun getBytes(index: Int, dst: ByteArray): ByteBuf = buf.getBytes(index, dst)
    override fun getBytes(index: Int, dst: ByteArray, dstIndex: Int, length: Int): ByteBuf =
        buf.getBytes(index, dst, dstIndex, length)

    override fun getBytes(index: Int, dst: ByteBuffer): ByteBuf = buf.getBytes(index, dst)

    @Throws(IOException::class)
    override fun getBytes(index: Int, out: OutputStream, length: Int): ByteBuf = buf.getBytes(index, out, length)

    @Throws(IOException::class)
    override fun getBytes(index: Int, out: GatheringByteChannel, length: Int): Int = buf.getBytes(index, out, length)

    @Throws(IOException::class)
    override fun getBytes(index: Int, out: FileChannel, position: Long, length: Int): Int =
        buf.getBytes(index, out, position, length)

    override fun getCharSequence(index: Int, length: Int, charset: Charset): CharSequence =
        buf.getCharSequence(index, length, charset)

    override fun setBoolean(index: Int, value: Boolean): ByteBuf = buf.setBoolean(index, value)
    override fun setByte(index: Int, value: Int): ByteBuf = buf.setByte(index, value)
    override fun setShort(index: Int, value: Int): ByteBuf = buf.setShort(index, value)
    override fun setShortLE(index: Int, value: Int): ByteBuf = buf.setShortLE(index, value)
    override fun setMedium(index: Int, value: Int): ByteBuf = buf.setMedium(index, value)
    override fun setMediumLE(index: Int, value: Int): ByteBuf = buf.setMediumLE(index, value)
    override fun setInt(index: Int, value: Int): ByteBuf = buf.setInt(index, value)
    override fun setIntLE(index: Int, value: Int): ByteBuf = buf.setIntLE(index, value)
    override fun setLong(index: Int, value: Long): ByteBuf = buf.setLong(index, value)
    override fun setLongLE(index: Int, value: Long): ByteBuf = buf.setLongLE(index, value)
    override fun setChar(index: Int, value: Int): ByteBuf = buf.setChar(index, value)
    override fun setFloat(index: Int, value: Float): ByteBuf = buf.setFloat(index, value)
    override fun setDouble(index: Int, value: Double): ByteBuf = buf.setDouble(index, value)
    override fun setBytes(index: Int, src: ByteBuf): ByteBuf = buf.setBytes(index, src)
    override fun setBytes(index: Int, src: ByteBuf, length: Int): ByteBuf = buf.setBytes(index, src, length)
    override fun setBytes(index: Int, src: ByteBuf, srcIndex: Int, length: Int): ByteBuf =
        buf.setBytes(index, src, srcIndex, length)

    override fun setBytes(index: Int, src: ByteArray): ByteBuf = buf.setBytes(index, src)
    override fun setBytes(index: Int, src: ByteArray, srcIndex: Int, length: Int): ByteBuf =
        buf.setBytes(index, src, srcIndex, length)

    override fun setBytes(index: Int, src: ByteBuffer): ByteBuf = buf.setBytes(index, src)

    @Throws(IOException::class)
    override fun setBytes(index: Int, input: InputStream, length: Int): Int = buf.setBytes(index, input, length)

    @Throws(IOException::class)
    override fun setBytes(index: Int, input: ScatteringByteChannel, length: Int): Int =
        buf.setBytes(index, input, length)

    @Throws(IOException::class)
    override fun setBytes(index: Int, input: FileChannel, position: Long, length: Int): Int =
        buf.setBytes(index, input, position, length)

    override fun setZero(index: Int, length: Int): ByteBuf = buf.setZero(index, length)
    override fun setCharSequence(index: Int, sequence: CharSequence, charset: Charset): Int =
        buf.setCharSequence(index, sequence, charset)

    override fun readBoolean(): Boolean = buf.readBoolean()
    override fun readByte(): Byte = buf.readByte()
    override fun readUnsignedByte(): Short = buf.readUnsignedByte()
    override fun readShort(): Short = buf.readShort()
    override fun readShortLE(): Short = buf.readShortLE()
    override fun readUnsignedShort(): Int = buf.readUnsignedShort()
    override fun readUnsignedShortLE(): Int = buf.readUnsignedShortLE()
    override fun readMedium(): Int = buf.readMedium()
    override fun readMediumLE(): Int = buf.readMediumLE()
    override fun readUnsignedMedium(): Int = buf.readUnsignedMedium()
    override fun readUnsignedMediumLE(): Int = buf.readUnsignedMediumLE()
    override fun readInt(): Int = buf.readInt()
    override fun readIntLE(): Int = buf.readIntLE()
    override fun readUnsignedInt(): Long = buf.readUnsignedInt()
    override fun readUnsignedIntLE(): Long = buf.readUnsignedIntLE()
    override fun readLong(): Long = buf.readLong()
    override fun readLongLE(): Long = buf.readLongLE()
    override fun readChar(): Char = buf.readChar()
    override fun readFloat(): Float = buf.readFloat()
    override fun readDouble(): Double = buf.readDouble()

    override fun readBytes(length: Int): ByteBuf = buf.readBytes(length)
    override fun readSlice(length: Int): ByteBuf = buf.readSlice(length)
    override fun readRetainedSlice(length: Int): ByteBuf = buf.readRetainedSlice(length)
    override fun readBytes(dst: ByteBuf): ByteBuf = buf.readBytes(dst)
    override fun readBytes(dst: ByteBuf, length: Int): ByteBuf = buf.readBytes(dst, length)
    override fun readBytes(dst: ByteBuf, dstIndex: Int, length: Int): ByteBuf = buf.readBytes(dst, dstIndex, length)
    override fun readBytes(dst: ByteArray): ByteBuf = buf.readBytes(dst)
    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int): ByteBuf = buf.readBytes(dst, dstIndex, length)
    override fun readBytes(dst: ByteBuffer): ByteBuf = buf.readBytes(dst)

    @Throws(IOException::class)
    override fun readBytes(out: OutputStream, length: Int): ByteBuf = buf.readBytes(out, length)

    @Throws(IOException::class)
    override fun readBytes(out: GatheringByteChannel, length: Int): Int = buf.readBytes(out, length)
    override fun readCharSequence(length: Int, charset: Charset): CharSequence = buf.readCharSequence(length, charset)

    @Throws(IOException::class)
    override fun readBytes(out: FileChannel, position: Long, length: Int): Int = buf.readBytes(out, position, length)
    override fun skipBytes(value: Int): ByteBuf = buf.skipBytes(value)

    override fun writeBoolean(value: Boolean): ByteBuf = buf.writeBoolean(value)
    override fun writeByte(value: Int): ByteBuf = buf.writeByte(value)
    override fun writeShort(value: Int): ByteBuf = buf.writeShort(value)
    override fun writeShortLE(value: Int): ByteBuf = buf.writeShortLE(value)
    override fun writeMedium(value: Int): ByteBuf = buf.writeMedium(value)
    override fun writeMediumLE(value: Int): ByteBuf = buf.writeMediumLE(value)
    override fun writeInt(value: Int): ByteBuf = buf.writeInt(value)
    override fun writeIntLE(value: Int): ByteBuf = buf.writeIntLE(value)
    override fun writeLong(value: Long): ByteBuf = buf.writeLong(value)
    override fun writeLongLE(value: Long): ByteBuf = buf.writeLongLE(value)
    override fun writeChar(value: Int): ByteBuf = buf.writeChar(value)
    override fun writeFloat(value: Float): ByteBuf = buf.writeFloat(value)
    override fun writeDouble(value: Double): ByteBuf = buf.writeDouble(value)
    override fun writeBytes(src: ByteBuf): ByteBuf = buf.writeBytes(src)
    override fun writeBytes(src: ByteBuf, length: Int): ByteBuf = buf.writeBytes(src, length)
    override fun writeBytes(src: ByteBuf, srcIndex: Int, length: Int): ByteBuf = buf.writeBytes(src, srcIndex, length)
    override fun writeBytes(src: ByteArray): ByteBuf = buf.writeBytes(src)
    override fun writeBytes(src: ByteArray, srcIndex: Int, length: Int): ByteBuf = buf.writeBytes(src, srcIndex, length)
    override fun writeBytes(src: ByteBuffer): ByteBuf = buf.writeBytes(src)

    @Throws(IOException::class)
    override fun writeBytes(input: InputStream, length: Int): Int = buf.writeBytes(input, length)

    @Throws(IOException::class)
    override fun writeBytes(input: ScatteringByteChannel, length: Int): Int = buf.writeBytes(input, length)

    @Throws(IOException::class)
    override fun writeBytes(input: FileChannel, position: Long, length: Int): Int =
        buf.writeBytes(input, position, length)

    override fun writeZero(length: Int): ByteBuf = buf.writeZero(length)
    override fun writeCharSequence(sequence: CharSequence, charset: Charset): Int =
        buf.writeCharSequence(sequence, charset)

    override fun indexOf(from: Int, to: Int, value: Byte): Int = buf.indexOf(from, to, value)

    override fun bytesBefore(value: Byte): Int = buf.bytesBefore(value)
    override fun bytesBefore(index: Int, value: Byte): Int = buf.bytesBefore(index, value)
    override fun bytesBefore(index: Int, length: Int, value: Byte): Int = buf.bytesBefore(index, length, value)

    override fun forEachByte(processor: ByteProcessor): Int = buf.forEachByte(processor)
    override fun forEachByte(index: Int, length: Int, processor: ByteProcessor): Int =
        buf.forEachByte(index, length, processor)

    override fun forEachByteDesc(processor: ByteProcessor): Int = buf.forEachByteDesc(processor)
    override fun forEachByteDesc(index: Int, length: Int, processor: ByteProcessor): Int =
        buf.forEachByteDesc(index, length, processor)

    override fun copy(): ByteBuf = buf.copy()
    override fun copy(index: Int, length: Int): ByteBuf = buf.copy(index, length)

    override fun slice(): ByteBuf = buf.slice()
    override fun retainedSlice(): ByteBuf = buf.retainedSlice()
    override fun slice(index: Int, length: Int): ByteBuf = buf.slice(index, length)
    override fun retainedSlice(index: Int, length: Int): ByteBuf = buf.retainedSlice(index, length)

    override fun duplicate(): ByteBuf = buf.duplicate()
    override fun retainedDuplicate(): ByteBuf = buf.retainedDuplicate()
    override fun nioBufferCount(): Int = buf.nioBufferCount()
    override fun nioBuffer(): ByteBuffer = buf.nioBuffer()

    override fun nioBuffer(index: Int, length: Int): ByteBuffer = buf.nioBuffer(index, length)
    override fun internalNioBuffer(index: Int, length: Int): ByteBuffer = buf.internalNioBuffer(index, length)
    override fun nioBuffers(): Array<ByteBuffer> = buf.nioBuffers()
    override fun nioBuffers(index: Int, length: Int): Array<ByteBuffer> = buf.nioBuffers(index, length)

    override fun hasArray(): Boolean = buf.hasArray()
    override fun array(): ByteArray = buf.array()
    override fun arrayOffset(): Int = buf.arrayOffset()

    override fun hasMemoryAddress(): Boolean = buf.hasMemoryAddress()
    override fun memoryAddress(): Long = buf.memoryAddress()

    override fun toString(charset: Charset): String = buf.toString(charset)
    override fun toString(index: Int, length: Int, charset: Charset): String = buf.toString(index, length, charset)

    override fun hashCode(): Int = buf.hashCode()
    override fun equals(other: Any?): Boolean = buf == other
    override fun compareTo(other: ByteBuf): Int = buf.compareTo(other)
    override fun toString(): String = buf.toString()

    override fun retain(p_retain_1_: Int): ByteBuf = buf.retain(p_retain_1_)
    override fun retain(): ByteBuf = buf.retain()
    override fun touch(): ByteBuf = buf.touch()
    override fun touch(p_touch_1_: Any): ByteBuf = buf.touch(p_touch_1_)
    override fun refCnt(): Int = buf.refCnt()
    override fun release(): Boolean = buf.release()
    override fun release(p_release_1_: Int): Boolean = buf.release(p_release_1_)

    private inline fun build(block: () -> Unit): CourierBuffer {
        block()
        return this
    }

    public companion object {
        /**
         * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using
         * readVarIntFromBuffer or writeVarIntToBuffer
         */
        @JvmStatic
        public fun getVarIntSize(input: Int): Int {
            for (i in 1..4) {
                if (input and -1 shl i * 7 == 0) {
                    return i
                }
            }
            return 5
        }
    }
}