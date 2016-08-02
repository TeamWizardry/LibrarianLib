package com.teamwizardry.librarianlib.fx.shader;

import com.teamwizardry.librarianlib.fx.shader.uniforms.FloatTypes;

public enum LibShaders {
	INSTANCE;
	
	public static Shader HUE;
	
	LibShaders() {
		initShaders();
	}
	
	private void initShaders() {
		HUE = new HueShader(null, "/assets/librarianlib/shaders/hue.frag");
		ShaderHelper.addShader(HUE);
	}
	
	public static class HueShader extends Shader {

		public FloatTypes.Float hue;

		public HueShader(String vert, String frag) {
			super(vert, frag);
		}
		
		@Override
		public void initUniforms() {
			hue = getUniform("hue");
		}

		@Override
		public void uniformDefaults() {
			hue.set(0);
		}
		
	}
}
