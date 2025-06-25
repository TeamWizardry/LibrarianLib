package com.teamwizardry.librarianlib.scribe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

@AutoCodec.Record
public data class TempThing(
    @AutoCodec.Field("Name") val name: String,
    @AutoCodec.Field("Value") val value: Int?,
    @AutoCodec.Field("Pos") val pos: BlockPos,
    @AutoCodec.Field("Values") val values: List<BlockPos>,
    @AutoCodec.Field("MutableValues") val mutableValues: MutableList<Int>,
    @AutoCodec.Field("StringMap") val stringMap: Map<String, Vec3d>,
    @AutoCodec.Field("IdentifierMap") val identifierMap: MutableMap<Identifier, Vec3d>,
    @AutoCodec.Field("BlockMap") val blockMap: MutableMap<BlockPos, Identifier>,
)