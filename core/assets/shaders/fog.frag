#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
//attributes from vertex shader
varying LOWP vec4 vColor;
varying vec2 vTexCoord;

//our texture samplers
uniform sampler2D u_texture;   //Breath map
uniform sampler2D u_normals;   //normal map

//values used for shading algorithm...
uniform vec2 resolution;         //resolution of screen
uniform vec3 BreathPos;           //light position, normalized
uniform LOWP vec4 BreathColor;    //light RGBA -- alpha is intensity
uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity
uniform vec3 Falloff;            //attenuation coefficients

uniform float frequency;
uniform float wavenumber;
uniform float time;

void main() {
	//sample fog color
	vec4 fogColor = texture2D(u_texture, vTexCoord);
	
	//the delta position of breath
	vec3 BreathDir = vec3(BreathPos.xy - (gl_FragCoord.xy / resolution.xy), BreathPos.z);
	
	//correct for aspect ratio
	BreathDir.x *= resolution.x / resolution.y;
	
	//determine distance (used for attenuation) BEFORE we normalize our BreathDir
	float D = length(BreathDir);
	
	//normalize our vectors
	vec3 N = normalize(vec3(0.5, 0.5, -0.1) * 2.0 - 1.0);
	vec3 L = normalize(BreathDir);
	
	//pre-multiply breath color with intensity
	//then perform "N dot L" to determine our Breath term
	vec3 Breath = (BreathColor.rgb * BreathColor.a) * 100.0 * max(dot(N, L), 0.01);

	//pre-multiply ambient color with intensity
	vec3 Ambient = AmbientColor.rgb * AmbientColor.a;
	
	//calculate attenuation
	//float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );
	float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );

	//calculate radial sinusoid
	Wave = cos(wavenumber*D + frequency*time)
	
	//the calculation which brings it all together
	vec3 Intensity = Ambient + Breath * Attenuation;
	vec3 FinalColor = BreathColor.rgb * Intensity;
	gl_FragColor = vColor * vec4(FinalColor, BreathColor.a * Wave * Wave);
}
