#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
//attributes from vertex shader
varying LOWP vec4 v_color;
varying vec2 v_texCoords;

//our texture samplers
uniform sampler2D u_texture;   //demist map

//values used for shading algorithm...
uniform vec2 resolution;         //resolution of screen
uniform vec3 demistPos;           //light position, normalized
uniform float spread;

void main() {
	//sample fog color
	vec4 fogColor = texture2D(u_texture, v_texCoords);
	
	//the delta position of demist
	vec3 demistDir = vec3(demistPos.xy - (gl_FragCoord.xy / resolution.xy), demistPos.z);
	
	//correct for aspect ratio
	demistDir.x *= resolution.x / resolution.y;
	
	//determine distance (used for attenuation) BEFORE we normalize our demistDir
	float D = length(demistDir);

	//calculate attenuation
	float Attenuation = 1.0 / ( 1.0 + ((0.3*D) + (8.0*D*D))/spread );
	
	gl_FragColor = v_color * vec4(fogColor.rgb, fogColor.a*(1.0 - Attenuation));
}
