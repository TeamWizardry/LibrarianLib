# Prism serialization

# Supported types

### Factories:
- arrays
- `java.util.List<T>`
- `@RefractClass` annotated classes
- `net.minecraftforge.registries.IForgeRegistryEntry<?>`
- `net.minecraftforge.common.util.INBTSerializable<?>`
- `net.minecraft.nbt.INBT`
- `kotlin.Pair<A, B>`
- `kotlin.Triple<A, B, C>`
- `net.minecraft.util.Tuple<A, B>`

### Concrete types:
#### Java stdlib types
- `double`
- `float`
- `long`
- `int`
- `short`
- `byte`
- `char`
- `boolean`
- `java.lang.Double`
- `java.lang.Float`
- `java.lang.Long`
- `java.lang.Integer`
- `java.lang.Short`
- `java.lang.Byte`
- `java.lang.Character`
- `java.lang.Boolean`
- `java.lang.Number`
- `double[]`
- `float[]`
- `long[]`
- `int[]`
- `short[]`
- `byte[]`
- `char[]`
- `boolean[]`
- `java.lang.String`
- `java.math.BigInteger`
- `java.math.BigDecimal`
- `java.util.BitSet`
- `java.util.UUID`
#### Minecraft types
- `com.mojang.authlib.GameProfile`
- `net.minecraft.block.BlockState`
- `net.minecraft.enchantment.EnchantmentData`
- `net.minecraft.item.ItemStack`
- `net.minecraft.potion.EffectInstance`
- `net.minecraft.text.ITextComponent`
- `net.minecraft.util.math.Box`
- `net.minecraft.util.math.BlockPos`
- `net.minecraft.util.math.ChunkPos`
- `net.minecraft.util.math.ColumnPos`
- `net.minecraft.util.math.GlobalPos`
- `net.minecraft.util.math.MutableBoundingBox`
- `net.minecraft.util.math.Rotations`
- `net.minecraft.util.math.SectionPos`
- `net.minecraft.util.math.Vec2f`
- `net.minecraft.util.math.Vector3d`
- `net.minecraft.util.Identifier`
- `net.minecraftforge.fluids.FluidStack`
#### LibLib types
- `com.teamwizardry.librarianlib.math.Matrix3d`
- `com.teamwizardry.librarianlib.math.Matrix4d`
- `com.teamwizardry.librarianlib.math.MutableMatrix3d`
- `com.teamwizardry.librarianlib.math.MutableMatrix4d`
- `com.teamwizardry.librarianlib.math.Quaternion`
- `com.teamwizardry.librarianlib.math.Ray2d`
- `com.teamwizardry.librarianlib.math.Rect2d`
- `com.teamwizardry.librarianlib.math.Vec2d`
- `com.teamwizardry.librarianlib.math.Vec2i`

There are a few notable absences at the moment, including maps, sets, and enums. As well as that, the list serializer 
doesn't recognize immutable lists yet and requires a no-arg constructor.
