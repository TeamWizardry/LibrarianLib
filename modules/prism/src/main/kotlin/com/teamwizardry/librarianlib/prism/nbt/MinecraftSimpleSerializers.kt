package com.teamwizardry.librarianlib.prism.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import com.teamwizardry.librarianlib.math.block
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentData
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.NumberNBT
import net.minecraft.nbt.StringNBT
import net.minecraft.potion.EffectInstance
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Tuple
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ColumnPos
import net.minecraft.util.math.GlobalPos
import net.minecraft.util.math.MutableBoundingBox
import net.minecraft.util.math.Rotations
import net.minecraft.util.math.SectionPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry

object ResourceLocationSerializer: NBTSerializer<ResourceLocation>() {
    override fun deserialize(tag: INBT, existing: ResourceLocation?): ResourceLocation {
        return ResourceLocation(tag.expectType<StringNBT>("tag").string)
    }

    override fun serialize(value: ResourceLocation): INBT {
        return StringNBT.valueOf(value.toString())
    }
}

//region Math stuff

object RotationsSerializer: NBTSerializer<Rotations>() {
    override fun deserialize(tag: INBT, existing: Rotations?): Rotations {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Rotations(
            tag.expect<NumberNBT>("X").float,
            tag.expect<NumberNBT>("Y").float,
            tag.expect<NumberNBT>("Z").float
        )
    }

    override fun serialize(value: Rotations): INBT {
        val tag = CompoundNBT()
        tag.put("X", FloatNBT.valueOf(value.x))
        tag.put("Y", FloatNBT.valueOf(value.y))
        tag.put("Z", FloatNBT.valueOf(value.z))
        return tag
    }
}

object Vec3dSerializer: NBTSerializer<Vec3d>() {
    override fun deserialize(tag: INBT, existing: Vec3d?): Vec3d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec3d(
            tag.expect<NumberNBT>("X").double,
            tag.expect<NumberNBT>("Y").double,
            tag.expect<NumberNBT>("Z").double
        )
    }

    override fun serialize(value: Vec3d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Z", DoubleNBT.valueOf(value.z))
        return tag
    }
}

object Vec2fSerializer: NBTSerializer<Vec2f>() {
    override fun deserialize(tag: INBT, existing: Vec2f?): Vec2f {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec2f(
            tag.expect<NumberNBT>("X").float,
            tag.expect<NumberNBT>("Y").float
        )
    }

    override fun serialize(value: Vec2f): INBT {
        val tag = CompoundNBT()
        tag.put("X", FloatNBT.valueOf(value.x))
        tag.put("Y", FloatNBT.valueOf(value.y))
        return tag
    }
}

object ChunkPosSerializer: NBTSerializer<ChunkPos>() {
    override fun deserialize(tag: INBT, existing: ChunkPos?): ChunkPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return ChunkPos(
            tag.expect<NumberNBT>("X").int,
            tag.expect<NumberNBT>("Z").int
        )
    }

    override fun serialize(value: ChunkPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Z", IntNBT.valueOf(value.z))
        return tag
    }
}

object ColumnPosSerializer: NBTSerializer<ColumnPos>() {
    override fun deserialize(tag: INBT, existing: ColumnPos?): ColumnPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return ColumnPos(
            tag.expect<NumberNBT>("X").int,
            tag.expect<NumberNBT>("Y").int
        )
    }

    override fun serialize(value: ColumnPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Y", IntNBT.valueOf(value.z))
        return tag
    }
}

object SectionPosSerializer: NBTSerializer<SectionPos>() {
    override fun deserialize(tag: INBT, existing: SectionPos?): SectionPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return SectionPos.from(
            ChunkPos(
                tag.expect<NumberNBT>("X").int,
                tag.expect<NumberNBT>("Z").int
            ),
            tag.expect<NumberNBT>("Y").int
        )
    }

    override fun serialize(value: SectionPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Y", IntNBT.valueOf(value.y))
        tag.put("Z", IntNBT.valueOf(value.z))
        return tag
    }
}

object GlobalPosSerializer: NBTSerializer<GlobalPos>() {
    private val registry by lazy {
        GameRegistry.findRegistry(DimensionType::class.java)
    }

    override fun deserialize(tag: INBT, existing: GlobalPos?): GlobalPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        val dimensionName = ResourceLocation(tag.expect<StringNBT>("Dimension").string)
        val dimension = registry.getValue(dimensionName)
            ?: throw DeserializationException("Unknown dimension type $dimensionName")
        return GlobalPos.of(
            dimension,
            block(
                tag.expect<NumberNBT>("X").int,
                tag.expect<NumberNBT>("Y").int,
                tag.expect<NumberNBT>("Z").int
            )
        )
    }

    override fun serialize(value: GlobalPos): INBT {
        val tag = CompoundNBT()
        tag.put("Dimension", StringNBT.valueOf(value.dimension.registryName.toString()))
        tag.put("X", IntNBT.valueOf(value.pos.x))
        tag.put("Y", IntNBT.valueOf(value.pos.y))
        tag.put("Z", IntNBT.valueOf(value.pos.z))
        return tag
    }
}

object AxisAlignedBBSerializer: NBTSerializer<AxisAlignedBB>() {
    override fun deserialize(tag: INBT, existing: AxisAlignedBB?): AxisAlignedBB {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return AxisAlignedBB(
            tag.expect<NumberNBT>("MinX").double,
            tag.expect<NumberNBT>("MinY").double,
            tag.expect<NumberNBT>("MinZ").double,
            tag.expect<NumberNBT>("MaxX").double,
            tag.expect<NumberNBT>("MaxY").double,
            tag.expect<NumberNBT>("MaxZ").double
        )
    }

    override fun serialize(value: AxisAlignedBB): INBT {
        val tag = CompoundNBT()
        tag.put("MinX", DoubleNBT.valueOf(value.minX))
        tag.put("MinY", DoubleNBT.valueOf(value.minY))
        tag.put("MinZ", DoubleNBT.valueOf(value.minZ))
        tag.put("MaxX", DoubleNBT.valueOf(value.maxX))
        tag.put("MaxY", DoubleNBT.valueOf(value.maxY))
        tag.put("MaxZ", DoubleNBT.valueOf(value.maxZ))
        return tag
    }
}

object MutableBoundingBoxSerializer: NBTSerializer<MutableBoundingBox>() {
    override fun deserialize(tag: INBT, existing: MutableBoundingBox?): MutableBoundingBox {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return MutableBoundingBox(
            tag.expect<NumberNBT>("MinX").int,
            tag.expect<NumberNBT>("MinY").int,
            tag.expect<NumberNBT>("MinZ").int,
            tag.expect<NumberNBT>("MaxX").int,
            tag.expect<NumberNBT>("MaxY").int,
            tag.expect<NumberNBT>("MaxZ").int
        )
    }

    override fun serialize(value: MutableBoundingBox): INBT {
        val tag = CompoundNBT()
        tag.put("MinX", IntNBT.valueOf(value.minX))
        tag.put("MinY", IntNBT.valueOf(value.minY))
        tag.put("MinZ", IntNBT.valueOf(value.minZ))
        tag.put("MaxX", IntNBT.valueOf(value.maxX))
        tag.put("MaxY", IntNBT.valueOf(value.maxY))
        tag.put("MaxZ", IntNBT.valueOf(value.maxZ))
        return tag
    }
}

//endregion

open class TupleSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Tuple<*, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TupleSerializer(prism, mirror as ClassMirror)
    }

    class TupleSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Tuple<Any?, Any?>>(type) {
        val firstSerializer by prism[type.typeParameters[0]]
        val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: INBT, existing: Tuple<Any?, Any?>?): Tuple<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // stupid @MethodsReturnNonnullByDefault
            return Tuple(
                if (tag.contains("A")) firstSerializer.read(tag.expect("A"), existing?.a) else null,
                if (tag.contains("B")) secondSerializer.read(tag.expect("B"), existing?.b) else null
            )
        }

        override fun serialize(value: Tuple<Any?, Any?>): INBT {
            val tag = CompoundNBT()
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.a?.also { tag.put("A", firstSerializer.write(it)) }
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.b?.also { tag.put("B", secondSerializer.write(it)) }
            return tag
        }
    }
}


object ITextComponentSerializer: NBTSerializer<ITextComponent>() {
    override fun deserialize(tag: INBT, existing: ITextComponent?): ITextComponent {
        return ITextComponent.Serializer.fromJson(tag.expectType<StringNBT>("tag").string)
            ?: inconceivable("ITextComponent.Serializer.fromJson doesn't seem to ever return null")
    }

    override fun serialize(value: ITextComponent): INBT {
        return StringNBT.valueOf(ITextComponent.Serializer.toJson(value))
    }
}

object ItemStackSerializer: NBTSerializer<ItemStack>() {
    override fun deserialize(tag: INBT, existing: ItemStack?): ItemStack {
        return ItemStack.read(tag.expectType("tag"))
    }

    override fun serialize(value: ItemStack): INBT {
        return value.write(CompoundNBT())
    }
}

object FluidStackSerializer: NBTSerializer<FluidStack>() {
    override fun deserialize(tag: INBT, existing: FluidStack?): FluidStack {
        return FluidStack.loadFluidStackFromNBT(tag.expectType("tag"))
    }

    override fun serialize(value: FluidStack): INBT {
        return value.writeToNBT(CompoundNBT())
    }
}

object EffectInstanceSerializer: NBTSerializer<EffectInstance>() {
    override fun deserialize(tag: INBT, existing: EffectInstance?): EffectInstance {
        return EffectInstance.read(tag.expectType("tag"))
    }

    override fun serialize(value: EffectInstance): INBT {
        return value.write(CompoundNBT())
    }
}

object EnchantmentDataSerializer: NBTSerializer<EnchantmentData>() {
    private val registry by lazy {
        GameRegistry.findRegistry(Enchantment::class.java)
    }

    override fun deserialize(tag: INBT, existing: EnchantmentData?): EnchantmentData {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        val dimensionName = ResourceLocation(tag.expect<StringNBT>("Enchantment").string)
        val enchantment = registry.getValue(dimensionName)
            ?: throw DeserializationException("Unknown enchantment type $dimensionName")
        return EnchantmentData(
            enchantment,
            tag.expect<NumberNBT>("Level").int
        )
    }

    override fun serialize(value: EnchantmentData): INBT {
        val tag = CompoundNBT()
        tag.put("Enchantment", StringNBT.valueOf(value.enchantment.registryName.toString()))
        tag.put("Level", IntNBT.valueOf(value.enchantmentLevel))
        return tag
    }
}
