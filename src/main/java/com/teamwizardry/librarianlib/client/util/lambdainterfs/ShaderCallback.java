package com.teamwizardry.librarianlib.client.util.lambdainterfs;


import com.teamwizardry.librarianlib.client.fx.shader.Shader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface ShaderCallback<T extends Shader> {
    void call(T shader);
}
