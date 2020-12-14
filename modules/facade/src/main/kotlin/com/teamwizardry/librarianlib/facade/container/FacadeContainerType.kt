package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.prism.Prisms
import com.teamwizardry.librarianlib.prism.nbt.NBTSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.PrismException
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.network.IContainerFactory
import net.minecraftforge.fml.network.NetworkHooks
import java.lang.IllegalArgumentException
import kotlin.math.min

public class FacadeContainerType<T: FacadeContainer> private constructor(
    private val factory: FacadeContainerFactory<T>
): ContainerType<T>(factory) {
    public constructor(clazz: Class<T>): this(FacadeContainerFactory(clazz))

    public fun open(player: ServerPlayerEntity, title: ITextComponent, vararg arguments: Any?) {
        NetworkHooks.openGui(player, object: INamedContainerProvider {
            override fun createMenu(windowId: Int, playerInv: PlayerInventory, player: PlayerEntity): Container? {
                return factory.create(windowId, player, arguments)
            }

            override fun getDisplayName(): ITextComponent {
                return title
            }
        }) { buffer ->
            factory.writeArguments(arguments, buffer)
        }
    }
}

private class FacadeContainerFactory<T: FacadeContainer>(clazz: Class<T>): IContainerFactory<T> {
    private val type = Mirror.reflectClass(clazz)
    private val constructor: ConstructorMirror
    private val argumentTypes: List<TypeMirror>
    private val argumentSerializers: List<NBTSerializer<*>>

    init {
        if (type.declaredConstructors.size != 1) {
            throw IllegalArgumentException("$type must have exactly one constructor")
        }
        constructor = type.declaredConstructors[0]
        if (
            constructor.parameters.size < 2 ||
            constructor.parameterTypes[0] != Mirror.types.int ||
            constructor.parameterTypes[1] != Mirror.reflect<PlayerEntity>()
        ) {
            val actualArgs = constructor.parameters.subList(0, min(2, constructor.parameters.size))
            throw IllegalArgumentException("The first two parameters of $type's constructor must be " +
                "`int windowId, PlayerEntity player`, not `${actualArgs.joinToString(", ")}`")
        }
        argumentTypes = constructor.parameterTypes.subList(2, constructor.parameterTypes.size)
        try {
            argumentSerializers = argumentTypes.map { Prisms.nbt[it].value }
        } catch (e: PrismException) {
            throw IllegalArgumentException("Unable to serialize the constructor for $type", e)
        }
    }

    override fun create(windowId: Int, playerInv: PlayerInventory, extraData: PacketBuffer): T {
        return create(windowId, playerInv.player, readArguments(extraData))
    }

    fun writeArguments(arguments: Array<out Any?>, buffer: PacketBuffer) {
        val list = ListNBT()
        argumentSerializers.mapIndexed { i, serializer ->
            val tag = CompoundNBT()
            arguments[i]?.also {
                tag.put("V", serializer.write(it))
            }
            list.add(tag)
        }.toTypedArray()
        buffer.writeCompoundTag(CompoundNBT().also { it.put("ll", list) })
    }

    fun readArguments(buffer: PacketBuffer): Array<Any?> {
        val list = buffer.readCompoundTag()!!.getList("ll", Constants.NBT.TAG_COMPOUND)
        return argumentSerializers.mapIndexed { i, serializer ->
            list.getCompound(i).get("V")?.let {
                serializer.read(it, null)
            }
        }.toTypedArray()
    }

    fun create(windowId: Int, player: PlayerEntity, arguments: Array<out Any?>): T {
        return constructor.call(windowId, player, *arguments)
    }
}