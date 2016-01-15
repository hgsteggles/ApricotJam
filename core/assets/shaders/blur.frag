#ifdef GL_ES 
#define LOWP lowp 
precision mediump float; 
#else 
#define LOWP  
#endif 
varying LOWP vec4 v_color; 
varying vec2 v_texCoords; 
 
uniform sampler2D u_texture; 
uniform vec2 resolution;         //resolution of screen
uniform float radius; 
uniform vec2 direction;
 
void main() { 
	vec4 sum = vec4(0.0); 
	vec2 tc = v_texCoords; 
	vec2 blur = radius/resolution.xy;  
     
    float hstep = direction.x; 
    float vstep = direction.y; 
     
	sum += texture2D(u_texture, vec2(tc.x - 4.0*blur.x*hstep, tc.y - 4.0*blur.y*vstep)) * 0.05; 
	sum += texture2D(u_texture, vec2(tc.x - 3.0*blur.x*hstep, tc.y - 3.0*blur.y*vstep)) * 0.09; 
	sum += texture2D(u_texture, vec2(tc.x - 2.0*blur.x*hstep, tc.y - 2.0*blur.y*vstep)) * 0.12; 
	sum += texture2D(u_texture, vec2(tc.x - 1.0*blur.x*hstep, tc.y - 1.0*blur.y*vstep)) * 0.15; 
	 
	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.16; 
	 
	sum += texture2D(u_texture, vec2(tc.x + 1.0*blur.x*hstep, tc.y + 1.0*blur.y*vstep)) * 0.15; 
	sum += texture2D(u_texture, vec2(tc.x + 2.0*blur.x*hstep, tc.y + 2.0*blur.y*vstep)) * 0.12; 
	sum += texture2D(u_texture, vec2(tc.x + 3.0*blur.x*hstep, tc.y + 3.0*blur.y*vstep)) * 0.09; 
	sum += texture2D(u_texture, vec2(tc.x + 4.0*blur.x*hstep, tc.y + 4.0*blur.y*vstep)) * 0.05; 
 
	gl_FragColor = v_color * vec4(sum.rgb, sum.a); 
}
