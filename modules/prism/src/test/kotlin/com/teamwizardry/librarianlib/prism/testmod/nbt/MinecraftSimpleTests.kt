package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.prism.nbt.AxisAlignedBBSerializer
import com.teamwizardry.librarianlib.prism.nbt.BlockPosSerializer
import com.teamwizardry.librarianlib.prism.nbt.BlockStateSerializer
import com.teamwizardry.librarianlib.prism.nbt.ChunkPosSerializer
import com.teamwizardry.librarianlib.prism.nbt.ColumnPosSerializer
import com.teamwizardry.librarianlib.prism.nbt.EffectInstanceSerializer
import com.teamwizardry.librarianlib.prism.nbt.EnchantmentDataSerializer
import com.teamwizardry.librarianlib.prism.nbt.FluidStackSerializer
import com.teamwizardry.librarianlib.prism.nbt.GameProfileSerializer
import com.teamwizardry.librarianlib.prism.nbt.INBTPassthroughSerializerFactory
import com.teamwizardry.librarianlib.prism.nbt.INBTSerializableSerializerFactory
import com.teamwizardry.librarianlib.prism.nbt.ITextComponentSerializerFactory
import com.teamwizardry.librarianlib.prism.nbt.ItemStackSerializer
import com.teamwizardry.librarianlib.prism.nbt.MutableBoundingBoxSerializer
import com.teamwizardry.librarianlib.prism.nbt.ResourceLocationSerializer
import com.teamwizardry.librarianlib.prism.nbt.RotationsSerializer
import com.teamwizardry.librarianlib.prism.nbt.SectionPosSerializer
import com.teamwizardry.librarianlib.prism.nbt.TupleSerializerFactory
import com.teamwizardry.librarianlib.prism.nbt.Vector2fSerializer
import com.teamwizardry.librarianlib.prism.nbt.Vector3dSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.enchantment.EnchantmentData
import net.minecraft.enchantment.Enchantments
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.ByteNBT
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.LongNBT
import net.minecraft.nbt.ShortNBT
import net.minecraft.nbt.StringNBT
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Tuple
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ColumnPos
import net.minecraft.util.math.GlobalPos
import net.minecraft.util.math.MutableBoundingBox
import net.minecraft.util.math.Rotations
import net.minecraft.util.math.SectionPos
import net.minecraft.util.math.vector.Vector2f
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

internal class MinecraftSimpleTests: NBTPrismTest() {
    @Test
    fun `read+write for ResourceLocation should be symmetrical`()
        = simple<ResourceLocation, ResourceLocationSerializer>(ResourceLocation("mod:name"), NBTBuilder.string("mod:name"))

    @Test
    fun `read for ResourceLocation with no namespace should read minecraft namespace`()
        = simpleRead<ResourceLocation, ResourceLocationSerializer>(ResourceLocation("minecraft:name"), NBTBuilder.string("name"))

    @Test
    fun `read+write for Vector3d should be symmetrical`() {
        simple<Vector3d, Vector3dSerializer>(Vector3d(1.0, 2.0, 3.0), NBTBuilder.compound {
            "X" *= double(1.0)
            "Y" *= double(2.0)
            "Z" *= double(3.0)
        })
    }

    @Test
    fun `read+write for Vector2f should be symmetrical`() {
        simple<Vector2f, Vector2fSerializer>(Vector2f(1f, 2f), NBTBuilder.compound {
            "X" *= float(1f)
            "Y" *= float(2f)
        }, { a, b -> a.x == b.x && a.y == b.y })
    }

    @Test
    fun `read+write for BlockPos should be symmetrical`() {
        simple<BlockPos, BlockPosSerializer>(BlockPos(1, 2, 3), NBTBuilder.compound {
            "X" *= int(1)
            "Y" *= int(2)
            "Z" *= int(3)
        })
    }

    @Test
    fun `read+write for ChunkPos should be symmetrical`() {
        simple<ChunkPos, ChunkPosSerializer>(ChunkPos(1, 2), NBTBuilder.compound {
            "X" *= int(1)
            "Z" *= int(2)
        })
    }

    @Test
    fun `read+write for ColumnPos should be symmetrical`() {
        simple<ColumnPos, ColumnPosSerializer>(ColumnPos(1, 2), NBTBuilder.compound {
            "X" *= int(1)
            "Z" *= int(2)
        })
    }

    @Test
    fun `read+write for SectionPos should be symmetrical`() {
        simple<SectionPos, SectionPosSerializer>(SectionPos.from(ChunkPos(1, 3), 2), NBTBuilder.compound {
            "X" *= int(1)
            "Y" *= int(2)
            "Z" *= int(3)
        })
    }

    @Test
    fun `read+write for Rotations should be symmetrical`() {
        simple<Rotations, RotationsSerializer>(Rotations(1f, 2f, 3f), NBTBuilder.compound {
            "X" *= float(1)
            "Y" *= float(2)
            "Z" *= float(3)
        })
    }

    @Test
    fun `read+write for AxisAlignedBB should be symmetrical`() {
        simple<AxisAlignedBB, AxisAlignedBBSerializer>(AxisAlignedBB(
            -1.0, -2.0, -3.0, 1.0, 2.0, 3.0
        ), NBTBuilder.compound {
            "MinX" *= double(-1)
            "MinY" *= double(-2)
            "MinZ" *= double(-3)
            "MaxX" *= double(1)
            "MaxY" *= double(2)
            "MaxZ" *= double(3)
        })
    }

    @Test
    fun `read for AxisAlignedBB should correct minmax swaps`() {
        simpleRead<AxisAlignedBB, AxisAlignedBBSerializer>(AxisAlignedBB(
            -1.0, -2.0, -3.0, 1.0, 2.0, 3.0
        ), NBTBuilder.compound {
            "MinX" *= double(1)
            "MinY" *= double(2)
            "MinZ" *= double(3)
            "MaxX" *= double(-1)
            "MaxY" *= double(-2)
            "MaxZ" *= double(-3)
        })
    }

    @Test
    fun `read+write for MutableBoundingBox should be symmetrical`() {
        simple<MutableBoundingBox, MutableBoundingBoxSerializer>(MutableBoundingBox(
            -1, -2, -3, 1, 2, 3
        ), NBTBuilder.compound {
            "MinX" *= int(-1)
            "MinY" *= int(-2)
            "MinZ" *= int(-3)
            "MaxX" *= int(1)
            "MaxY" *= int(2)
            "MaxZ" *= int(3)
        }, { a, b ->
            a.minX == b.minX && a.minY == b.minY && a.minZ == b.minZ &&
                a.maxX == b.maxX && a.maxY == b.maxY && a.maxZ == b.maxZ
        })
    }

    @Test
    fun `read+write for Tuple should be symmetrical`() {
        simple<Tuple<String, Int>, TupleSerializerFactory.TupleSerializer>(Tuple("test", 10), NBTBuilder.compound {
            "A" *= string("test")
            "B" *= int(10)
        }, { a, b -> a.a == b.a && a.b == b.b })
    }

    @Test
    fun `read+write for Tuple with a null value should exclude that key`() {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // stupid @MethodsReturnNonnullByDefault
        simple<Tuple<String, Int?>, TupleSerializerFactory.TupleSerializer>(Tuple<String, Int?>("test", null), NBTBuilder.compound {
            "A" *= string("test")
        }, { a, b -> a.a == b.a && a.b == b.b })
    }

    inline fun<reified T: INBT> nbtPassthroughTest(original: T, shouldCopy: Boolean) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(INBTPassthroughSerializerFactory.INBTPassthroughSerializer::class.java, serializer.javaClass)

        val serialized = serializer.write(original)
        assertEquals(original, serialized)
        if(shouldCopy)
            assertNotSame(original, serialized)
        else
            assertSame(original, serialized)

        val deserialized = serializer.read(serialized, null)
        assertEquals(original, deserialized)
        if(shouldCopy)
            assertNotSame(serialized, deserialized)
        else
            assertSame(serialized, deserialized)
    }

    @Test
    fun `read+write for CompoundNBT should return a copy of the tag`()
        = nbtPassthroughTest(NBTBuilder.compound { "Foo" *= string("bar") }, true)
    @Test
    fun `read+write for ListNBT should return a copy of the tag`()
        = nbtPassthroughTest(NBTBuilder.list { n+ string("foo") }, true)
    @Test
    fun `read+write for LongArrayNBT should return a copy of the tag`()
        = nbtPassthroughTest(NBTBuilder.longArray(1, 2, 3), true)
    @Test
    fun `read+write for IntArrayNBT should return a copy of the tag`()
        = nbtPassthroughTest(NBTBuilder.intArray(1, 2, 3), true)
    @Test
    fun `read+write for ByteArrayNBT should return a copy of the tag`()
        = nbtPassthroughTest(NBTBuilder.byteArray(1, 2, 3), true)

    @Test
    fun `read+write for StringNBT should return the tag`()
        = nbtPassthroughTest(StringNBT.valueOf("foo"), false)
    @Test
    fun `read+write for DoubleNBT should return the tag`()
        = nbtPassthroughTest(DoubleNBT.valueOf(1.0), false)
    @Test
    fun `read+write for FloatNBT should return the tag`()
        = nbtPassthroughTest(FloatNBT.valueOf(1f), false)
    @Test
    fun `read+write for LongNBT should return the tag`()
        = nbtPassthroughTest(LongNBT.valueOf(1), false)
    @Test
    fun `read+write for IntNBT should return the tag`()
        = nbtPassthroughTest(IntNBT.valueOf(1), false)
    @Test
    fun `read+write for ShortNBT should return the tag`()
        = nbtPassthroughTest(ShortNBT.valueOf(1), false)
    @Test
    fun `read+write for ByteNBT should return the tag`()
        = nbtPassthroughTest(ByteNBT.valueOf(1), false)

    @Test
    fun `read for NBT passthrough with the wrong tag type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<ByteNBT>()].value.read(StringNBT.valueOf("oops!"), null)
        }
    }

    @Test
    fun `read+write for ITextComponent should be symmetrical`() {
        simple<ITextComponent, ITextComponentSerializerFactory.ITextComponentSerializer>(
            StringTextComponent("value"), StringNBT.valueOf("""
                {"text":"value"}
            """.trimIndent())
        )
    }

    @Test
    fun `read+write for GameProfile should be symmetrical`() {
        simple<GameProfile, GameProfileSerializer>(
            GameProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), "Notch"),
            NBTBuilder.compound {
                "Id" *= string("069a79f4-44e9-4726-a5be-fca90e38aaf5")
                "Name" *= string("Notch")
            }
        )
    }

    @Test
    fun `read+write for BlockState should be symmetrical`() {
        simple<BlockState, BlockStateSerializer>(
            Blocks.BONE_BLOCK.stateContainer.validStates[0],
            NBTBuilder.compound {
                "Name" *= string("minecraft:bone_block")
                "Properties" *= compound {
                    "axis" *= string("x")
                }
            }
        )
    }

    @Test
    fun `read+write for ItemStack should be symmetrical`() {
        val stack = ItemStack(Items.DIAMOND, 16)
        simple<ItemStack, ItemStackSerializer>(
            stack,
            NBTBuilder.compound {
                "id" *= string("minecraft:diamond")
                "Count" *= byte(16)
            },
            { a, b -> ItemStack.areItemStacksEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b) }
        )
    }

    @Test
    fun `read+write for ItemStack with NBT should be symmetrical`() {
        val stack = ItemStack(Items.DIAMOND, 16)
        stack.tag = NBTBuilder.compound {
            "Custom" *= string("value")
        }
        simple<ItemStack, ItemStackSerializer>(
            stack,
            NBTBuilder.compound {
                "id" *= string("minecraft:diamond")
                "Count" *= byte(16)
                "tag" *= compound {
                    "Custom" *= string("value")
                }
            },
            { a, b -> ItemStack.areItemStacksEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b) }
        )
    }

    @Test
    fun `read+write for FluidStack should be symmetrical`() {
        val stack = FluidStack(Fluids.WATER, 750)
        simple<FluidStack, FluidStackSerializer>(
            stack,
            NBTBuilder.compound {
                "FluidName" *= string("minecraft:water")
                "Amount" *= int(750)
            },
            { a, b -> a.isFluidStackIdentical(b) }
        )
    }

    @Test
    fun `read+write for FluidStack with NBT should be symmetrical`() {
        val stack = FluidStack(Fluids.WATER, 750)
        stack.tag = NBTBuilder.compound {
            "Custom" *= string("value")
        }
        simple<FluidStack, FluidStackSerializer>(
            stack,
            NBTBuilder.compound {
                "FluidName" *= string("minecraft:water")
                "Amount" *= int(750)
                "Tag" *= compound {
                    "Custom" *= string("value")
                }
            },
            { a, b -> a.isFluidStackIdentical(b) }
        )
    }

    @Test
    fun `read+write for EffectInstance should be symmetrical`() {
        val stack = EffectInstance(Effects.JUMP_BOOST, 20, 2)
        simple<EffectInstance, EffectInstanceSerializer>(
            stack,
            NBTBuilder.compound {
                "Id" *= byte(8)
                "Amplifier" *= byte(2)
                "Duration" *= int(20)
                "Ambient" *= byte(0)
                "ShowParticles" *= byte(1)
                "ShowIcon" *= byte(1)
                "CurativeItems" *= list {
                    n+ compound {
                        "id" *= string("minecraft:milk_bucket")
                        "Count" *= byte(1)
                    }
                }
            }
        )
    }

    @Test
    fun `read+write for EnchantmentData should be symmetrical`() {
        val data = EnchantmentData(Enchantments.SHARPNESS, 3)
        simple<EnchantmentData, EnchantmentDataSerializer>(
            data,
            NBTBuilder.compound {
                "Enchantment" *= string("minecraft:sharpness")
                "Level" *= int(3)
            },
            { a, b -> a.enchantment == b.enchantment && a.enchantmentLevel == b.enchantmentLevel }
        )
    }

}