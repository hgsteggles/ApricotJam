package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.AnimatedShaderComponent;
import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.AnimationSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PipeFillTestScreen extends BasicScreen {
	private ShaderProgram shader;
	private String VERT = createVert();
	private String FRAG = createFrag();
	
	public PipeFillTestScreen(SpacePanic spacePanic) {
		super(spacePanic);
		
		add(new AnimationSystem());
		
		add(createFluid());
		add(createPipe());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (shader != null)
			shader.dispose();
	}
	
	private Entity createPipe() {
		Entity tile = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = MiscArt.pipesRegion[0];

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 1);
		
		tile.add(textureComp).add(transComp);
		
		return tile;
	}
	
	private Entity createFluid() {
		Entity tile = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = MiscArt.pipeFluidTestMaskRegion[0];

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 0);
		
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(0, new Animation(0.1f, MiscArt.pipeFluidTestMaskRegion));
		
		StateComponent stateComp = new StateComponent();
		stateComp.set(0);
		
		AnimatedShaderComponent animShaderComp = new AnimatedShaderComponent();
		shader = new ShaderProgram(VERT, FRAG);
		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}
		if (shader.getLog().length() != 0)
			System.out.println(shader.getLog());
		animShaderComp.shader = shader;
		
		tile.add(textureComp).add(transComp).add(animComp).add(stateComp).add(animShaderComp);

		return tile;
	}
	
	private String createVert() {
		String vert =  
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
		return vert;
	}
	
	private String createFrag() {
		String frag = 
				"varying vec2 vTexCoord;\n" + 
				"uniform float time;\n" + 
				"uniform sampler2D u_texture;\n" + 
				
				"// Helper constants\n" + 
				"#define F2 0.366025403\n" + 
				"#define G2 0.211324865\n" + 
				"#define K 0.0243902439 // 1/41\n" + 
				"\n" +
				"// Permutation polynomial\n" + 
				"float permute(float x) {\n" + 
				"  return mod((34.0 * x + 1.0)*x, 289.0);\n" + 
				"}\n" + 
				"\n" +
				"// Gradient mapping with an extra rotation.\n" + 
				"vec2 grad2(vec2 p, float rot) {\n" + 
				"#if 1\n" + 
				"// Map from a line to a diamond such that a shift maps to a rotation.\n" + 
				"  float u = permute(permute(p.x) + p.y) * K + rot; // Rotate by shift\n" + 
				"  u = 4.0 * fract(u) - 2.0;\n" + 
				"  return vec2(abs(u)-1.0, abs(abs(u+1.0)-2.0)-1.0);\n" + 
				"#else\n" + 
				"#define TWOPI 6.28318530718\n" + 
				"// For more isotropic gradients, sin/cos can be used instead.\n" + 
				"  float u = permute(permute(p.x) + p.y) * K + rot; // Rotate by shift\n" + 
				"  u = fract(u) * TWOPI;\n" + 
				"  return vec2(cos(u), sin(u));\n" + 
				"#endif\n" + 
				"}\n" + 
				"\n" + 
				"float srdnoise(in vec2 P, in float rot, out vec2 grad) {\n" + 
				"\n" + 
				"  // Transform input point to the skewed simplex grid\n" + 
				"  vec2 Ps = P + dot(P, vec2(F2));\n" + 
				"\n" + 
				"  // Round down to simplex origin\n" + 
				"  vec2 Pi = floor(Ps);\n" + 
				"\n" + 
				"  // Transform simplex origin back to (x,y) system\n" + 
				"  vec2 P0 = Pi - dot(Pi, vec2(G2));\n" + 
				"\n" + 
				"  // Find (x,y) offsets from simplex origin to first corner\n" + 
				"  vec2 v0 = P - P0;\n" + 
				"\n" + 
				"  // Pick (+x, +y) orFRAG (+y, +x) increment sequence\n" + 
				"  vec2 i1 = (v0.x > v0.y) ? vec2(1.0, 0.0) : vec2 (0.0, 1.0);\n" + 
				"\n" + 
				"  // Determine the offsets for the other two corners\n" + 
				"  vec2 v1 = v0 - i1 + G2;\n" + 
				"  vec2 v2 = v0 - 1.0 + 2.0 * G2;\n" + 
				"\n" + 
				"  // Wrap coordinates at 289 to avoid float precision problems\n" + 
				"  Pi = mod(Pi, 289.0);\n" + 
				"\n" + 
				"  // Calculate the circularly symmetric part of each noise wiggle\n" + 
				"  vec3 t = max(0.5 - vec3(dot(v0,v0), dot(v1,v1), dot(v2,v2)), 0.0);\n" + 
				"  vec3 t2 = t*t;\n" + 
				"  vec3 t4 = t2*t2;\n" + 
				"\n" + 
				"  // Calculate the gradients for the three corners\n" + 
				"  vec2 g0 = grad2(Pi, rot);\n" + 
				"  vec2 g1 = grad2(Pi + i1, rot);\n" + 
				"  vec2 g2 = grad2(Pi + 1.0, rot);\n" + 
				"\n" + 
				"  // Compute noise contributions from each corner\n" + 
				"  vec3 gv = vec3(dot(g0,v0), dot(g1,v1), dot(g2,v2)); // ramp: g dot v\n" + 
				"  vec3 n = t4 * gv;  // Circular kernel times linear ramp\n" + 
				"\n" + 
				"  // Compute partial derivatives in x and y\n" + 
				"  vec3 temp = t2 * t * gv;\n" + 
				"  vec3 gradx = temp * vec3(v0.x, v1.x, v2.x);\n" + 
				"  vec3 grady = temp * vec3(v0.y, v1.y, v2.y);\n" + 
				"  grad.x = -8.0 * (gradx.x + gradx.y + gradx.z);\n" + 
				"  grad.y = -8.0 * (grady.x + grady.y + grady.z);\n" + 
				"  grad.x += dot(t4, vec3(g0.x, g1.x, g2.x));\n" + 
				"  grad.y += dot(t4, vec3(g0.y, g1.y, g2.y));\n" + 
				"  grad *= 40.0;\n" + 
				"\n" +
				"  // Add contributions from the three corners and return\n" + 
				"  return 40.0 * (n.x + n.y + n.z);\n" + 
				"}\n" + 
				"\n" +
				"void main(void) {\n" + 
				"  vec2 g1, g2;\n" + 
				"  vec2 p = 0.06*gl_FragCoord.xy;\n" + 
				"  float mtime = 0.6*time;\n" +
				"  float n1 = srdnoise(p*0.5, 0.2*mtime, g1);\n" + 
				"  float n2 = srdnoise(p*2.0 + g1*0.5, 0.51*mtime, g2);\n" + 
				"  float n3 = srdnoise(p*4.0 + g1*0.5 + g2*0.25, 0.77*mtime, g2);\n" +
				"  float mask = texture2D(u_texture, vTexCoord).a;\n" + 
				"  gl_FragColor = vec4(vec3(0.2, 0.5, 0.6) + 0.4*vec3(n1+0.75*n2+0.5*n3), mask);\n" + 
				"}";
		
		return frag;
	}
}
