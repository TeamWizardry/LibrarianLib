package com.teamwizardry.librarianlib.core.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
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

    public void register(IFutureReloadListener listener) {
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(listener);
    }

    public <T> void register(ISimpleReloadListener<T> listener) {
        this.register(new SimpleReloadListener<T>(listener));
    }

    public void register(List<IResourceType> types, SidedRunnable.Client runnable) {
        ISelectiveResourceReloadListener listener = (resourceManager, resourcePredicate) -> {
            boolean hasMatch = false;
            for (IResourceType type : types) {
                hasMatch = hasMatch || resourcePredicate.test(type);
            }
            if(hasMatch)
                runnable.run();
        };

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(listener);
    }

    public void register(IResourceType type, SidedRunnable.Client runnable) {
        register(Lists.newArrayList(type), runnable);
    }

    private static class SimpleReloadListener<T> extends ReloadListener<T> {
        private ISimpleReloadListener<T> listener;

        public SimpleReloadListener(ISimpleReloadListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
            return listener.prepare(resourceManagerIn, profilerIn);
        }

        @Override
        protected void apply(T splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
            listener.apply(splashList, resourceManagerIn, profilerIn);
        }
    }
}
