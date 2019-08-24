package com.teamwizardry.librarianlib.core.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.SelectiveReloadStateHandler;

import java.util.List;

public class ResourceReload {
    @OnlyIn(Dist.CLIENT)
    public boolean isLoading(IResourceType type) {
        return SelectiveReloadStateHandler.INSTANCE.get().test(type);
    }

    public void register(List<IResourceType> types, ClientRunnable runnable) {
        ISelectiveResourceReloadListener listener = (resourceManager, resourcePredicate) -> {
            boolean hasMatch = false;
            for (IResourceType type : types) {
                hasMatch = hasMatch || resourcePredicate.test(type);
            }
            if(hasMatch)
                runnable.runIfClient();
        };

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(listener);
    }

    public void register(IResourceType type, ClientRunnable runnable) {
        register(Lists.newArrayList(type), runnable);
    }

}
