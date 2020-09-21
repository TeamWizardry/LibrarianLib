package com.teamwizardry.librarianlib.facade.container.messaging

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.prism.Prisms
import com.teamwizardry.librarianlib.prism.nbt.NBTSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.prism.PrismException
import dev.thecodewarrior.prism.utils.annotation
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraftforge.common.util.Constants

public object MessageScanner {
    private val scanCache = mutableMapOf<Class<*>, List<MessageScan>>()

    public fun getMessages(targetType: Class<*>): List<MessageScan> {
        return scanCache.getOrPut(targetType) {
            Mirror.reflectClass(targetType).methods.mapNotNull {
                if(it.isAnnotationPresent<Message>())
                    MessageScan(it)
                else
                    null
            }.unmodifiableView()
        }
    }

    public class MessageScan(public val method: MethodMirror) {
        public val name: String = method.annotation<Message>()!!.name.let {
            if(it == "") method.name else it
        }
        public val parameterSerializers: List<NBTSerializer<*>> = method.parameters.map { parameter ->
            try {
                Prisms.nbt[parameter.type].value
            } catch(e: PrismException) {
                throw IllegalStateException("Error getting serializer for parameter ${parameter.index} of message '$name'")
            }
        }.unmodifiableView()

        public fun writeArguments(arguments: Array<out Any?>): CompoundNBT {
            val list = ListNBT()
            parameterSerializers.mapIndexed { i, serializer ->
                val tag = CompoundNBT()
                arguments[i]?.also {
                    tag.put("V", serializer.write(it))
                }
                list.add(tag)
            }.toTypedArray()
            return CompoundNBT().also { it.put("ll", list) }
        }

        public fun readArguments(payload: CompoundNBT): Array<Any?> {
            val list = payload.getList("ll", Constants.NBT.TAG_COMPOUND)
            return parameterSerializers.mapIndexed { i, serializer ->
                list.getCompound(i).get("V")?.let {
                    serializer.read(it, null)
                }
            }.toTypedArray()
        }
    }
}
