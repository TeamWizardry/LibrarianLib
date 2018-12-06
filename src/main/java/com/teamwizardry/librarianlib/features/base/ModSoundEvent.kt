package com.teamwizardry.librarianlib.features.base

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.convertJSON
import com.teamwizardry.librarianlib.features.kotlin.serialize
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.utilities.generatedFiles
import com.teamwizardry.librarianlib.features.utilities.getPathForSounds
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.File

/**
 * @author WireSegal
 * Created at 7:58 PM on 2/11/17.
 */
@Suppress("LeakingThis")
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
open class ModSoundEvent(name: String, subtitle: String?, private val sounds: List<String>) : SoundEvent(rl(name)) {

    constructor(name: String, subtitle: String?, vararg sounds: String) : this(name, subtitle, listOf(*sounds))
    constructor(name: String, subtitle: String?) : this(name, subtitle, listOf(rl(name).toString()))
    constructor(name: String) : this(name, null)

    private val modid = currentModId

    private val subtitle = subtitle?.let { modid + ".subtitle." + VariantHelper.toSnakeCase(subtitle) }
    private val id = rl(name)

    open fun name(): String = id.path
    open fun sounds(): Iterable<String> = sounds
    open fun subtitle(): String? = subtitle

    open fun json(): JsonElement {
        val obj = JsonObject()
        obj.add("sounds", convertJSON(sounds()))
        val sub = subtitle()
        if (sub != null)
            obj.addProperty("subtitle", sub)
        return obj
    }


    // Internal

    init {
        RegistrationHandler.register(this, id)
        modSounds.getOrPut(modid) { mutableListOf() }.add(this)
    }

    companion object {
        @JvmStatic
        fun simple(name: String): ModSoundEvent = ModSoundEvent(name, name)

        private fun rl(name: String) = ResourceLocation(currentModId, VariantHelper.toSnakeCase(name))

        private val modSounds = mutableMapOf<String, MutableList<ModSoundEvent>>()

        private fun shouldGenerateAnyJson() = LibrarianLib.DEV_ENVIRONMENT && LibLibConfig.generateJson


        private fun serialize(el: JsonElement)
                = if (LibLibConfig.prettyJsonSerialization) el.serialize() else el.toString() + "\n"

        private fun log(text: String) {
            if (LibrarianLib.DEV_ENVIRONMENT) LibrarianLog.info(text)
        }

        @JvmStatic
        @SideOnly(Side.CLIENT)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun onSoundRegistry(event: RegistryEvent.Register<SoundEvent>) {
            if (shouldGenerateAnyJson()) {
                for ((mod, sounds) in modSounds) {
                    if (mod in OwnershipHandler.DEV_OWNED) {
                        log("$mod | Registering sounds in json")
                        val modpad = " " * mod.length
                        val soundJsonPath = getPathForSounds(mod)
                        val file = File(soundJsonPath)
                        file.parentFile.mkdirs()
                        file.createNewFile()

                        var flag = false

                        val json = try {
                            JsonParser().parse(file.reader()).asJsonObject
                        } catch (ignored: Throwable) {
                            flag = true
                            JsonObject()
                        }

                        for (sound in sounds) {
                            val name = sound.name()
                            log("$modpad | Registering sound $name")
                            if (!json.has(name)) {
                                json.add(name, sound.json())
                                log("$modpad | Successfully registered $name!")
                                flag = true
                            }
                        }

                        if (flag) {
                            log("$modpad | Writing new sound json")
                            file.writeText(serialize(json))
                            generatedFiles.add(soundJsonPath)
                        }
                    }
                }
            }
        }
    }
}
