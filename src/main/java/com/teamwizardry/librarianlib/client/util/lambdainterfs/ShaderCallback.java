package com.teamwizardry.librarianlib.client.util.lambdainterfs;


import com.teamwizardry.librarianlib.client.fx.shader.Shader;

@FunctionalInterface
public interface ShaderCallback<T extends Shader> {
    void call(T shader);
}
