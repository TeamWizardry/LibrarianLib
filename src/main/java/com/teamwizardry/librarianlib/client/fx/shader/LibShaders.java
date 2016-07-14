package com.teamwizardry.librarianlib.client.fx.shader;

import com.teamwizardry.librarianlib.client.fx.shader.uniforms.FloatTypes;

public enum LibShaders {
	INSTANCE;
	
	public static Shader HUE;
	
	private LibShaders() {
		initShaders();
	}
	
	private void initShaders() {
		HUE = new HueShader(null, "/assets/librarianlib/shaders/hue.frag");
		ShaderHelper.addShader(HUE);
	}
	
	public static class HueShader extends Shader {

		public HueShader(String vert, String frag) {
			super(vert, frag);
		}

		public FloatTypes.Float hue;
		
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
