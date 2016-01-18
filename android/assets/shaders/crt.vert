attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;
// Define some calculations that will be used in fragment shader.
varying vec2 one;
varying float mod_factor;

void main() {
	v_texCoords = a_texCoord0;

	gl_Position = u_projTrans * a_position;
}
