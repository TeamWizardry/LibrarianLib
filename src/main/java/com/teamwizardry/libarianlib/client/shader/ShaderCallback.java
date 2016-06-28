package com.teamwizardry.libarianlib.client.shader;

public abstract class ShaderCallback<T extends Shader> {

    public abstract void call(T shader);

}
