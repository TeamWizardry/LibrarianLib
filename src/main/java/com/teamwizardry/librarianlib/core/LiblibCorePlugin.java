package com.teamwizardry.librarianlib.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

//-Dfml.coreMods.load=com.teamwizardry.librarianlib.core.LiblibCorePlugin
@IFMLLoadingPlugin.Name("LibrarianLib Plugin")
@IFMLLoadingPlugin.TransformerExclusions("com.teamwizardry.librarianlib")
public class LiblibCorePlugin implements IFMLLoadingPlugin {

    public LiblibCorePlugin() {
        MixinBootstrap.init();
        //for when liblib will need mixins itself todo
        //Mixins.addConfiguration("com/teamwizardry/librarianlib/core/mixins_config.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
