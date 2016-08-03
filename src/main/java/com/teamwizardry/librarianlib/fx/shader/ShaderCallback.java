package com.teamwizardry.librarianlib.fx.shader;

@FunctionalInterface
public interface ShaderCallback<T extends Shader> {

    void call(T shader);

}
