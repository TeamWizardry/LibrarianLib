package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.INBT
import net.minecraft.nbt.StringNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

internal open class IForgeRegistryEntrySerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<IForgeRegistryEntry<*>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return IForgeRegistryEntrySerializer(mirror as ClassMirror)
    }

    class IForgeRegistryEntrySerializer(type: ClassMirror): NBTSerializer<IForgeRegistryEntry<*>>(type) {
        private val registryType = type.getSuperclass(IForgeRegistryEntry::class.java).typeParameters[0].erasure
        private val registry: IForgeRegistry<*> by lazy {
            @Suppress("UNCHECKED_CAST")
            GameRegistry.findRegistry(registryType as Class<DummyRegistryEntry>)
                ?: throw DeserializationException("Could not find registry for ${registryType.simpleName}")
        }

        /**
         * The type checker doesn't like accessing `IForgeRegistry<*>.registryName`, so we have to cast it down to an
         * `IForgeRegistry<DummyRegistryEntry>` in order to access the name.
         */
        private val registryName: ResourceLocation by lazy {
            @Suppress("UNCHECKED_CAST")
            (registry as IForgeRegistry<DummyRegistryEntry>).registryName
        }

        override fun deserialize(tag: INBT, existing: IForgeRegistryEntry<*>?): IForgeRegistryEntry<*> {
            val entryName = ResourceLocation(tag.expectType<StringNBT>("tag").string)
            return registry.getValue(entryName)
                ?: throw DeserializationException("Could not find entry $entryName in $registryName")
        }

        override fun serialize(value: IForgeRegistryEntry<*>): INBT {
            return StringNBT.valueOf(value.registryName.toString())
        }

        /**
         * Since I can't cast a `Class<*>` to satisfy the `K extends IForgeRegistryEntry<K>` requirement of
         * [GameRegistry.findRegistry], I have to take advantage of the fact that generic parameters in casts aren't
         * type checked at runtime by casting to a dummy registry entry.
         */
        private abstract class DummyRegistryEntry: IForgeRegistryEntry<DummyRegistryEntry>
    }
}

// Dimension types seem to be "dynamic registry entries" now, which means you need to get them from a World instance.
///**
// * For some reason [DimensionType] is an [IForgeRegistryEntry], yet the DimensionType registry isn't an
// * [IForgeRegistry], and thus isn't present in [GameRegistry.findRegistry]
// */
//internal object DimensionTypeSerializer: NBTSerializer<DimensionType>() {
//    override fun deserialize(tag: INBT, existing: DimensionType?): DimensionType {
//        val entryName = ResourceLocation(tag.expectType<StringNBT>("tag").string)
//        @Suppress("DEPRECATION")
//        return Registry.DIMENSION_TYPE_KEY.getValue(entryName).get() // DIMENSION_TYPE has a default value
//    }
//
//    override fun serialize(value: DimensionType): INBT {
//        return StringNBT.valueOf(value.registryName.toString())
//    }
//}
