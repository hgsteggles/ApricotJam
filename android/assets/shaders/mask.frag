//attributes from vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_mask;   //normal map

//values used for shading algorithm...
uniform vec2 resolution;         //resolution of screen
uniform vec4 maskRect;           //maskRectangle, normalised

float withinBounds(vec2 pos, vec4 rect) {
	if (pos.x <= rect.x || pos.x > rect.x + rect.z || pos.y <= rect.y || pos.y > rect.y + rect.w) {
		return 0.0;
	}
	else {
		return 1.0;
	}
}

void main() {
	//sample color of our texture
	vec4 texColor0 = texture2D(u_texture, v_texCoords);
	vec2 p = (gl_FragCoord.xy / resolution.xy);

	gl_FragColor = v_color * vec4(texColor0.rgb, texColor0.a*withinBounds(p, maskRect));
}
