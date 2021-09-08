package com.teamwizardry.librarianlib.facade.container.messaging

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.scribe.Scribe
import com.teamwizardry.librarianlib.scribe.nbt.NbtSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.prism.PrismException
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList

public object MessageScanner {
    private val scanCache = mutableMapOf<Class<*>, List<MessageScan>>()

    public fun getMessages(targetType: Class<*>): List<MessageScan> {
        return scanCache.getOrPut(targetType) {
            Mirror.reflectClass(targetType).methods.mapNotNull {
                if(it.annotations.isPresent<Message>())
                    MessageScan(it)
                else
                    null
            }.unmodifiableView()
        }
    }

    public class MessageScan(public val method: MethodMirror) {
        private val messageAnnotation = method.annotations.get<Message>()!!
        public val name: String = messageAnnotation.name.let {
            if(it == "") method.name else it
        }

        public val side: MessageSide = messageAnnotation.side

        public val parameterSerializers: List<NbtSerializer<*>> = method.parameters.map { parameter ->
            try {
                Scribe.nbt[parameter.type].value
            } catch(e: PrismException) {
                throw IllegalStateException("Error getting serializer for parameter ${parameter.index} of message '$name'", e)
            }
        }.unmodifiableView()

        public fun writeArguments(arguments: Array<out Any?>): NbtCompound {
            val list = NbtList()
            parameterSerializers.mapIndexed { i, serializer ->
                val tag = NbtCompound()
                arguments[i]?.also {
                    tag.put("V", serializer.write(it))
                }
                list.add(tag)
            }.toTypedArray()
            return NbtCompound().also { it.put("ll", list) }
        }

        public fun readArguments(payload: NbtCompound): Array<Any?> {
            val list = payload.getList("ll", NbtCompound().type.toInt())
            return parameterSerializers.mapIndexed { i, serializer ->
                list.getCompound(i).get("V")?.let {
                    serializer.read(it)
                }
            }.toTypedArray()
        }
    }
}
