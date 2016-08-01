package com.teamwizardry.librarianlib.fx.shader;

@FunctionalInterface
public interface ShaderCallback<T extends Shader> {

    public void call(T shader);

}
