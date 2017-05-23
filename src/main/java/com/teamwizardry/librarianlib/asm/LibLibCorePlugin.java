package com.teamwizardry.librarianlib.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

//-Dfml.coreMods.load=com.teamwizardry.librarianlib.asm.LibLibCorePlugin

@IFMLLoadingPlugin.Name("LibrarianLib Plugin")
@IFMLLoadingPlugin.TransformerExclusions("com.teamwizardry.librarianlib")
public class LibLibCorePlugin implements IFMLLoadingPlugin {

    public static boolean runtimeDeobf = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                "com.teamwizardry.librarianlib.asm.LibLibTransformer"
        };
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
        runtimeDeobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
