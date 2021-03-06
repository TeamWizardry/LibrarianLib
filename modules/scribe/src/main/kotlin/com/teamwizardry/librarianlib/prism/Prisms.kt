package com.teamwizardry.librarianlib.prism

import com.teamwizardry.librarianlib.prism.nbt.*
import dev.thecodewarrior.prism.Prism

public object Prisms {
    @JvmStatic
    public val nbt: NBTPrism = Prism<NBTSerializer<*>>().also { prism ->
        prism.register(
            // java types
            ArraySerializerFactory(prism),
            ListSerializerFactory(prism),
            ObjectSerializerFactory(prism),
            EnumSerializerFactory(prism),

            // kotlin types
            PairSerializerFactory(prism),
            TripleSerializerFactory(prism),

            // minecraft types
            IForgeRegistryEntrySerializerFactory(prism),
            INBTSerializableSerializerFactory(prism),
            INBTPassthroughSerializerFactory(prism),
            TupleSerializerFactory(prism),
            ITextComponentSerializerFactory(prism)
        )

        prism.register(
            // primitives
            PrimitiveLongSerializer,
            PrimitiveIntSerializer,
            PrimitiveShortSerializer,
            PrimitiveByteSerializer,
            PrimitiveCharSerializer,
            PrimitiveDoubleSerializer,
            PrimitiveFloatSerializer,
            PrimitiveBooleanSerializer,

            // boxed
            LongSerializer,
            IntegerSerializer,
            ShortSerializer,
            ByteSerializer,
            CharacterSerializer,
            DoubleSerializer,
            FloatSerializer,
            BooleanSerializer,
            NumberSerializer,

            // primitive arrays
            PrimitiveLongArraySerializer,
            PrimitiveIntArraySerializer,
            PrimitiveShortArraySerializer,
            PrimitiveByteArraySerializer,
            PrimitiveCharArraySerializer,
            PrimitiveDoubleArraySerializer,
            PrimitiveFloatArraySerializer,
            PrimitiveBooleanArraySerializer,

            // java types
            BigIntegerSerializer,
            BigDecimalSerializer,
            StringSerializer,
            BitSetSerializer,
            UUIDSerializer,

            // minecraft types
            BlockPosSerializer,
            Vector3dSerializer,
            Vector2fSerializer,
            ChunkPosSerializer,
            ColumnPosSerializer,
            SectionPosSerializer,
//            GlobalPosSerializer, // DimensionType serialization issues
            RotationsSerializer,
            AxisAlignedBBSerializer,
            MutableBoundingBoxSerializer,
            ResourceLocationSerializer,
            BlockStateSerializer,
            GameProfileSerializer,
            ItemStackSerializer,
            FluidStackSerializer,
            EffectInstanceSerializer,
            EnchantmentDataSerializer,
//            DimensionTypeSerializer, // DimensionType serialization issues
            FluidTankSerializer,

            // liblib types
            Vec2dSerializer,
            Vec2iSerializer,
            Rect2dSerializer,
            Matrix3dSerializer,
            MutableMatrix3dSerializer,
            Matrix4dSerializer,
            MutableMatrix4dSerializer,
            QuaternionSerializer
        )
    }
}