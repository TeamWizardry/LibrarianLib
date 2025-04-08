package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.bridge.InjectedTranslations
import net.minecraft.util.Identifier
import pers.solid.brrp.v1.api.LanguageProvider
import pers.solid.brrp.v1.api.RuntimeResourcePack

public class TestModResourceManager(public val modid: String, logManager: ModLogManager) {
    private val logger = logManager.makeLogger("TestModContentManager")

    public val runtimeResourcePack: RuntimeResourcePack = RuntimeResourcePack.create(Identifier.of("$modid:test_resources"))
    @Suppress("UnstableApiUsage")
    public val lang: LanguageProvider = LanguageProvider.create()

    /**
     * Register the language entries, overwriting if necessary.
     */
    internal fun writeLang() {
        runtimeResourcePack.addLang(Identifier.of(modid, "en_us"), lang)
        InjectedTranslations.translations.putAll(lang.content())
    }
}