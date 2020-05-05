# Prism serialization

# Supported types
## Java types:
- Any array
- Any `List`
- Any `@RefractClass` object
- `Double`, `Float`, `Long`, `Integer`, `Short`, `Character`, `Byte`, `Boolean`, `Number`, `BigInteger`, `BigDecimal`
- `double`, `float`, `long`, `int`, `short`, `char`, `byte`, `boolean`
- `double[]`, `float[]`, `long[]`, `int[]`, `short[]`, `char[]`, `byte[]`, `boolean[]`, `BitSet`
- `String`, `UUID`
## Kotlin types:
- `Pair`, `Triple`
## Minecraft types:
- Any `IForgeRegistryEntry`
- Any NBT type
- `BlockPos`, `Vec3d`, `Vec2f`, `ChunkPos`, `ColumnPos`, `SectionPos`, `GlobalPos`, `Rotations`, `AxisAlignedBB`, `MutableBoundingBox`
- `ResourceLocation`, `BlockState`, `GameProfile`, `ITextComponent`, `INBTSerializable`, `Tuple`
- `ItemStack`, `FluidStack`, `EffectInstance`, `EnchantmentData`
## LibLib types:
- `Vec2d`, `Vec2i`, `Ray2d`, `Rect2d`, `Matrix3d`, `MutableMatrix3d`, `Matrix4d`, `MutableMatrix4d`, `Quaternion`

There are a few notable absences at the moment, including maps, sets, and enums. As well as that, the list serializer 
doesn't recognize immutable lists yet and requires a no-arg constructor.

