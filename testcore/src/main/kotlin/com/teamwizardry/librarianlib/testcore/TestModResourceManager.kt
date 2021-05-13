package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.bridge.InjectedTranslations
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.lang.JLang
import net.minecraft.util.Identifier

public class TestModResourceManager(public val modid: String, logManager: ModLogManager) {
    private val logger = logManager.makeLogger("TestModContentManager")

    public val arrp: RuntimeResourcePack = RuntimeResourcePack.create("$modid:test_resources")
    public val lang: JLang = JLang.lang()

    /**
     * Register the language entries, overwriting if necessary.
     */
    internal fun writeLang() {
        arrp.addLang(Identifier(modid, "en_us"), lang)
        InjectedTranslations.translations.putAll(lang.lang)
    }
}