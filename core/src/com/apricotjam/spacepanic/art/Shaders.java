package com.apricotjam.spacepanic.art;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.components.FBO_Component;
import com.apricotjam.spacepanic.components.FBO_ItemComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.thesecretpie.shader.ShaderManager;

public class Shaders {
	public static ShaderManager manager;
	private static AssetManager shaderAssets;
	
	//our constants...
	public static final float DEFAULT_LIGHT_Z = 0.15f;
	public static final float AMBIENT_INTENSITY = 0.6f;
	public static final float LIGHT_INTENSITY = 1.0f*0.4f;
	
	public static final Vector3 LIGHT_POS = new Vector3(0.5f, 0.5f, DEFAULT_LIGHT_Z);
	public static final Vector3 LIGHT_COLOR = new Vector3(1f, 1.0f, 1.0f);
	public static final Vector3 AMBIENT_COLOR = new Vector3(1.0f, 1.0f, 1f);
	public static final Vector3 FALLOFF = new Vector3(0.4f, 0.25f*3f, 0.10f*20f);
	
	public static final Vector3 DEMIST_POS = new Vector3(0.5f, 0.5f, 0.15f);
	
	
	static public void load() {
		shaderAssets = new AssetManager();
		manager = new ShaderManager("shaders", shaderAssets);
		
		ShaderProgram.pedantic = false;
		
		manager.add("fluid", Gdx.files.internal("default.vert"), Gdx.files.internal("fluid.frag"));
		
		manager.add("light", Gdx.files.internal("light.vert"), Gdx.files.internal("light.frag"));
		manager.registerResolutionShader("light");
		manager.begin("light");
		manager.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
		manager.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
		manager.setUniformf("Falloff", FALLOFF.x, FALLOFF.y, FALLOFF.z);
		manager.setUniformf("LightPos", LIGHT_POS.x, LIGHT_POS.y, LIGHT_POS.z);
		manager.end();
		
		manager.add("helmet-light", Gdx.files.internal("light.vert"), Gdx.files.internal("light.frag"));
		manager.registerResolutionShader("helmet-light");
		manager.begin("helmet-light");
		manager.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, 1.0f*0.4f);
		manager.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 0.6f);
		manager.setUniformf("Falloff", 0.4f, 0.25f*3f, 0.10f*20f);
		manager.setUniformf("LightPos", LIGHT_POS.x, LIGHT_POS.y, LIGHT_POS.z);
		manager.end();
		
		manager.add("crt", Gdx.files.internal("crt.vert"), Gdx.files.internal("crt.frag"));
		manager.begin("crt");
		manager.setUniformf("time", 0f);
		manager.setUniformf("tint", 1f, 1f, 0.85f);
		//manager.setUniformf("offset", 0.003f);
		manager.setUniformf("chromaticDispersion", -0.1f, -0.1f);
		manager.setUniformf("Distortion", 0.3f);
		manager.setUniformf("zoom", 1.0f);
		manager.end();
		
		manager.add("mask", Gdx.files.internal("default.vert"), Gdx.files.internal("mask.frag"));
		manager.registerResolutionShader("mask");
		manager.begin("mask");
		manager.setUniformf("maskRect", 0.25f, 0.25f, 0.5f, 0.5f);
		manager.end();
		
		manager.add("led-blur", Gdx.files.internal("default.vert"), Gdx.files.internal("blur.frag"));
		manager.registerResolutionShader("led-blur");
		manager.begin("led-blur");
		manager.setUniformf("radius", 0.2f);
		manager.end();
		
		manager.add("led-blur-mask", Gdx.files.internal("default.vert"), Gdx.files.internal("led.frag"));
		manager.registerResolutionShader("led-blur-mask");
		manager.begin("led-blur-mask");
		manager.setUniformf("radius", 0.2f);
		manager.end();
		
		manager.add("fog", Gdx.files.internal("default.vert"), Gdx.files.internal("fog.frag"));
		manager.registerResolutionShader("fog");
		manager.begin("fog");
		manager.setUniformf("demistPos", DEMIST_POS.x, DEMIST_POS.y, DEMIST_POS.z);
		manager.setUniformf("spread", 2f);
		manager.end();
		
		manager.resize(SpacePanic.WIDTH, SpacePanic.HEIGHT);
	}
	
	final static String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			
			"uniform mat4 u_projTrans;\n" + 
			" \n" + 
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			
			"void main() {\n" +  
			"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";
	
	//no changes except for LOWP for color values
	//we would store this in a file for increased readability
	final static String FRAG = 
			//GL ES specific stuff
			  "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"//attributes from vertex shader\n" + 
			"varying LOWP vec4 vColor;\n" + 
			"varying vec2 vTexCoord;\n" + 
			"\n" + 
			"//our texture samplers\n" + 
			"uniform sampler2D u_texture;   //diffuse map\n" + 
			"uniform sampler2D u_normals;   //normal map\n" + 
			"\n" + 
			"//values used for shading algorithm...\n" + 
			"uniform vec2 Resolution;         //resolution of screen\n" + 
			"uniform vec3 LightPos;           //light position, normalized\n" + 
			"uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity\n" + 
			"uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity \n" + 
			"uniform vec3 Falloff;            //attenuation coefficients\n" + 
			"\n" + 
			"void main() {\n" + 
			"	//RGBA of our diffuse color\n" + 
			"	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);\n" + 
			"	\n" + 
			"	//RGB of our normal map\n" + 
			"	vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;\n" + 
			"	\n" + 
			"	//The delta position of light\n" + 
			"	vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);\n" + 
			"	\n" + 
			"	//Correct for aspect ratio\n" + 
			"	LightDir.x *= Resolution.x / Resolution.y;\n" + 
			"	\n" + 
			"	//Determine distance (used for attenuation) BEFORE we normalize our LightDir\n" + 
			"	float D = length(LightDir);\n" + 
			"	\n" + 
			"	//normalize our vectors\n" + 
			"	vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" + 
			"	vec3 L = normalize(LightDir);\n" + 
			"	\n" + 
			"	//Pre-multiply light color with intensity\n" + 
			"	//Then perform \"N dot L\" to determine our diffuse term\n" + 
			"	vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);\n" + 
			"\n" + 
			"	//pre-multiply ambient color with intensity\n" + 
			"	vec3 Ambient = AmbientColor.rgb * AmbientColor.a;\n" + 
			"	\n" + 
			"	//calculate attenuation\n" + 
			"	float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );\n" + 
			"	\n" + 
			"	//the calculation which brings it all together\n" + 
			"	vec3 Intensity = Ambient + Diffuse * Attenuation;\n" + 
			"	vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" + 
			"	gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);\n" + 
			"}";
	
	static public void dispose() {
		if (shaderAssets != null)
			shaderAssets.dispose();
		if (manager != null)
			manager.dispose();
	}

	public static FBO_Component generateFBOComponent(String fbo_ID, TextureComponent textureComponent) {
		int xsize = (int)(textureComponent.size.x * SpacePanic.WIDTH / BasicScreen.WORLD_WIDTH);
		int ysize = (int)(textureComponent.size.y * SpacePanic.HEIGHT / BasicScreen.WORLD_HEIGHT);
		manager.createFB(fbo_ID, xsize, ysize);
		FBO_Component fbo_component = new FBO_Component();
		fbo_component.FBO_ID = fbo_ID;
		fbo_component.batch = Shaders.manager.getSpriteBatch(fbo_ID);
		fbo_component.camera = new OrthographicCamera(textureComponent.size.x, textureComponent.size.y);
		return fbo_component;
	}

	public static FBO_ItemComponent generateFBOItemComponent(String fbo_ID) {
		FBO_ItemComponent fbo_itemComponent = new FBO_ItemComponent();
		fbo_itemComponent.fboID = fbo_ID;
		fbo_itemComponent.fboBatch = manager.getSpriteBatch(fbo_ID);
		return fbo_itemComponent;
	}
}
