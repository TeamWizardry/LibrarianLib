package com.teamwizardry.librarianlib.scribe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos

@AutoFactory
public interface Animal
@AutoElement
public class Dog : Animal
@AutoElement
public class Cat : Animal

internal object Foo {

//    @AutoCodec.Record
//    data class TempThing(
//        @AutoCodec.Field("Name") val name: String,
//        @AutoCodec.Field("Value") val value: Int?,
//    ) {
//        companion object {
//            val CODEC = TempThingCodecs.CODEC
//        }
//    }

}

@AutoCodec.Record
public data class TempThing(
    @AutoCodec.Field("Name") val name: String,
    @AutoCodec.Field("Value") val value: Int?,
    @AutoCodec.Field("Pos") val pos: BlockPos,
)

internal val TEMP_CODEC = RecordCodecBuilder.create {
    it.group(
        Codec.STRING.fieldOf("Name").forGetter(TempThing::name),
        Codec.INT.fieldOf("Value").forGetter(TempThing::value),
        BlockPos.CODEC.fieldOf("Pos").forGetter(TempThing::pos),
    ).apply(it, ::TempThing)
}